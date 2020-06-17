package com.goodreadme.rest

import com.goodreadme.CannotCreatePullRequest
import com.goodreadme.entities.Repository
import com.goodreadme.entities.github.*
import com.goodreadme.updater.Base64Updater
import com.goodreadme.updater.VersionFinder
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
    gitHubToken: String,
    private val client: HttpClient
) {

    private val updater = Base64Updater()
    private val versionFinder = VersionFinder()

    private val tokenHeaderValue = "token $gitHubToken"
    private val tokenHeaderKey = "Authorization"

    suspend fun updateReadMe(release: Repository): Success {
        val originRepo = client.get<GitHubRepository>(
            UrlProvider.getRepoUrl(release)
        )
        val releases = client.get<List<Release>>(
            UrlProvider.getReleasesUrl(
                originRepo
            )
        )
        val versions = versionFinder.findVersions(releases)

        deleteOldRepo(originRepo)

        val forkRepo = makeForkRepo(client, originRepo, tokenHeaderKey, tokenHeaderValue)
        val readMe = client.get<ProjectReadMe>(
            UrlProvider.getReadMeUrl(
                forkRepo
            )
        )
        val newReadMeContent = updater.updateReadMeBase64(readMe.content, versions)

        client.put<String>(readMe.url) {
            val request = FileUpdateRequest(
                sha = readMe.sha,
                content = newReadMeContent
            )
            this.body = jsonSerializer.write(request)
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }

        val prResponse = createPullRequest(client, originRepo, forkRepo)

        return Success(prResponse.htmlUrl)
    }

    private suspend fun deleteOldRepo(originRepo: GitHubRepository) {
        val botUser = client.get<User>(UrlProvider.getCurrentUserUrl()) {
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }

        try {
            client.delete<String>(UrlProvider.getOldRepoUrl(originRepo, botUser)) {
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
            }
        } catch (e: Exception) {
            //ignore error in this case
        }
    }

    private suspend fun createPullRequest(
        client: HttpClient,
        originRepo: GitHubRepository,
        forkRepo: GitHubRepository
    ): PRResponse {
        val pullRequestBody = jsonSerializer.write(
            PRRequest(
                head = "${forkRepo.user.login}:${originRepo.defaultBranch}",
                base = originRepo.defaultBranch
            )
        )
        logger.info(pullRequestBody.toString())
        return try {
            client.post(UrlProvider.getPullsUrl(originRepo)) {
                this.headers.append(tokenHeaderKey, tokenHeaderValue)
                this.body = pullRequestBody
            }
        } catch (e: Exception) {
            logger.error("Cannot create PULL REQUEST", e)
            throw CannotCreatePullRequest(originRepo)
        }
    }

    private suspend fun makeForkRepo(
        client: HttpClient,
        repo: GitHubRepository,
        tokenHeaderKey: String,
        tokenHeaderValue: String
    ): GitHubRepository {
        return client.post(repo.forkUrl) {
            this.headers.append(tokenHeaderKey, tokenHeaderValue)
        }
    }
}
