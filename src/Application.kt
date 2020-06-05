package com.vova

import com.fasterxml.jackson.databind.SerializationFeature
import com.vova.rest.RestController
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
        level = Level.INFO
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
    val restController = RestController(log, defaultSerializer(), gitHubToken)

    routing {
        post("/webhook/github") {
            call.respond(restController.handleGitHubHook(call.receive(), client))
        }
    }
}