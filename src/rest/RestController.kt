package com.vova.rest

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

sealed class GitHubHookResult
data class Success(val prUrl: String) : GitHubHookResult()
data class Error(val urlToProblemRepo: String) : GitHubHookResult()

class RestController(
    private val logger: Logger,
    private val jsonSerializer: JsonSerializer,
    gitHubToken: String
) {

    private val updater = Base64Updater()
    private val versionFinder = VersionFinder()

    private val tokenHeaderValue = "token $gitHubToken"
    private val tokenHeaderKey = "Authorization"
    private val pullsSuffix = "/pulls"

    suspend fun handleGitHubHook(release: ReleaseHook, client: HttpClient): GitHubHookResult {
        val originRepo = release.repository
        val forkRepo = makeForkRepo(client, release, tokenHeaderKey, tokenHeaderValue)
        val prResponse = try {
            val readMe = client.get<ProjectReadMe>("${forkRepo.url}/readme")
            val releases = client.get<List<Release>>(originRepo.releasesUrl)
            val newReadMeContent = updater.updateReadMeBase64(readMe.content, versionFinder.findVersions(releases))

            client.put<String>(readMe.url) {
                val request = FileUpdateRequest(sha = readMe.sha, content = newReadMeContent)
                this.body = jsonSerializer.write(request)
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
            }

            client.post<PRResponse>(originRepo.url + pullsSuffix) {
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
                this.body = jsonSerializer.write(
                    PRRequest(
                        head = "${forkRepo.owner.login}:${forkRepo.defaultBranch}",
                        base = forkRepo.defaultBranch
                    )
                )
            }
        } catch (e: Exception) {
            logger.error("Cannot create PR", e)
            return Error(originRepo.url)
        } finally {
            client.delete<String>(forkRepo.url) {
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
            }
        }

        return Success(prResponse.htmlUrl)
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
