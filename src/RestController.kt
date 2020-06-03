package com.vova

import com.vova.entities.FileUpdateRequest
import com.vova.entities.ProjectReadMe
import com.vova.entities.ReleaseHook
import com.vova.entities.Repository
import com.vova.updater.Base64Updater
import io.ktor.client.HttpClient
import io.ktor.client.features.json.defaultSerializer
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.http.HttpStatusCode
import org.slf4j.Logger

class RestController(private val log: Logger) {

    private val updater = Base64Updater()

    private val token = "6f40ae05b8a587659b05045fdc58bcb0bbff1f94"
    private val tokenHeaderValue = "token $token"
    private val tokenHeaderKey = "Authorization"

    suspend fun handleGitHubHook(release: ReleaseHook, client: HttpClient): HttpStatusCode {
        val forkRepo = forkRepo(client, release, tokenHeaderKey, tokenHeaderValue)
        val readMe = client.get<ProjectReadMe>("${forkRepo.url}/readme")
        val newReadMeContent = updater.updateReadMeBase64(readMe.content)

        log.error("newReadMeContent $newReadMeContent")

        client.put<String>(readMe.url) {
            val json = defaultSerializer()
            val request = FileUpdateRequest(sha = readMe.sha, content = newReadMeContent)
            this.body = json.write(request)
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
            this.headers.append("Content-Transfer-Encoding", "base64")
        }

        return HttpStatusCode.Accepted
    }

    private suspend fun forkRepo(
        client: HttpClient,
        release: ReleaseHook,
        tokenHeaderKey: String,
        tokenHeaderValue: String
    ): Repository {
        return client.post(release.repository.forkUrl) {
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }
    }
}