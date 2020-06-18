package com.goodreadme

import com.fasterxml.jackson.databind.SerializationFeature
import com.goodreadme.entities.FullNameRequest
import com.goodreadme.entities.Repository
import com.goodreadme.entities.github.GitHubReleaseHook
import com.goodreadme.rest.RestController
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.ContentTransformationException
import io.ktor.request.path
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CallLogging) {
        level = Level.TRACE
        filter { call -> call.request.path().startsWith("/") }
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        exception<CannotFoundTwoCorrectRelease> {
            call.respond(HttpStatusCode.PreconditionFailed, "error" to "Repo must have at lease two releases")
        }
        exception<CannotCreatePullRequest> {
            call.respond(HttpStatusCode.PreconditionFailed, "error" to it.originRepo)
        }
        exception<ContentTransformationException> {
            call.respond(HttpStatusCode.UnprocessableEntity)
        }
        exception<NothingToUpdate> {
            call.respond(HttpStatusCode.PreconditionFailed, it.message.toString())
        }
    }

    val client = HttpClient(CIO) {
        install(JsonFeature) {
            serializer = JacksonSerializer()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.BODY
        }
    }

    val gitHubToken = environment.config.property("ktor.github.token").getString()
    val clientSecret = environment.config.property("ktor.github.clientsecret").getString()
    val controller =
        RestController(log, defaultSerializer(), gitHubToken, client)

    routing {
        post("/checkMe/byReleaseWebHook") {
            val hook = call.receive<GitHubReleaseHook>()
            if (hook.action != "published") {
                call.respond(HttpStatusCode.UnprocessableEntity)
                return@post
            }

            call.respond(
                controller.updateReadMe(
                    Repository(
                        hook.repository.user.login,
                        hook.repository.name
                    )
                )
            )
        }

        post("/checkMe/byRepoFullName") {
            val fullNameRequest = call.receive<FullNameRequest>()
            val fullNameArgs = fullNameRequest.fullName.split('/')
            try {
                call.respond(
                    controller.updateReadMe(
                        Repository(
                            fullNameArgs[0],
                            fullNameArgs[1]
                        )
                    )
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.UnprocessableEntity)
            }
        }

        post("/checkMe/byRepoPojo") {
            val repo = call.receive<Repository>()
            call.respond(controller.updateReadMe(repo))
        }
    }
}
