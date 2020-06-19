package com.goodreadme.rest

import com.goodreadme.entities.Repository
import com.goodreadme.entities.github.GitHubRepository
import com.goodreadme.entities.github.User

object UrlProvider {

    private const val pullsSuffix = "/pulls"

    fun getCurrentUserUrl(): String {
        return "https://api.github.com/user"
    }

    fun getRepoUrl(repository: Repository): String {
        return "https://api.github.com/repos/${repository.owner}/${repository.repo}"
    }

    fun getOldRepoUrl(originRepo: GitHubRepository, botUser: User): String {
        return originRepo.url.replace(originRepo.user.login, botUser.login)
    }

    fun getReleasesUrl(gitHubRepository: GitHubRepository): String {
        return gitHubRepository.releasesUrl
            .removeSuffix("{/id}")
            .removeSuffix("/")
    }

    fun getReadMeUrl(gitHubRepository: GitHubRepository): String {
        return gitHubRepository.url
            .removeSuffix("/")
            .plus("/readme")
    }

    fun getPullsUrl(gitHubRepository: GitHubRepository): String {
        return gitHubRepository.url + pullsSuffix
    }
}
