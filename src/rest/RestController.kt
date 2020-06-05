package com.vova.rest

import com.vova.CannotCreatePullRequest
import com.vova.entities.*
import com.vova.updater.Base64Updater
import com.vova.updater.VersionFinder
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonSerializer
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import org.slf4j.Logger

data class Success(val prUrl: String)

class RestController(
    private val logger: Logger,
    private val jsonSerializer: JsonSerializer,
    gitHubToken: String
) {

    private val updater = Base64Updater()
    private val versionFinder = VersionFinder()

    private val tokenHeaderValue = "token $gitHubToken"
    private val tokenHeaderKey = "Authorization"

    suspend fun handleGitHubHook(release: ReleaseHook, client: HttpClient): Success {
        val originRepo = release.repository
        val forkRepo = makeForkRepo(client, release, tokenHeaderKey, tokenHeaderValue)

        val readMe = client.get<ProjectReadMe>(UrlProvider.getReadMeUrl(forkRepo))
        val releases = client.get<List<Release>>(UrlProvider.getReleasesUrl(originRepo))
        val newReadMeContent = updater.updateReadMeBase64(readMe.content, versionFinder.findVersions(releases))

        client.put<String>(readMe.url) {
            val request = FileUpdateRequest(sha = readMe.sha, content = newReadMeContent)
            this.body = jsonSerializer.write(request)
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }

        val prResponse = createPullRequest(client, originRepo, forkRepo)

        client.delete<String>(forkRepo.url) {
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }

        return Success(prResponse.htmlUrl)
    }

    private suspend fun createPullRequest(
        client: HttpClient,
        originRepo: Repository,
        forkRepo: Repository
    ): PRResponse {
        return try {
            client.post(UrlProvider.getPullsUrl(originRepo)) {
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
                this.body = jsonSerializer.write(
                    PRRequest(
                        head = "${forkRepo.owner.login}:${forkRepo.defaultBranch}",
                        base = forkRepo.defaultBranch
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Cannot create repo", e)
            throw CannotCreatePullRequest(originRepo)
        }
    }

    private suspend fun makeForkRepo(
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
