package com.vova.rest

import com.vova.entities.Repository
import com.vova.entities.User

object UrlProvider {

    private const val pullsSuffix = "/pulls"

    fun getCurrentUserUrl(): String {
        return "https://api.github.com/user"
    }

    fun getOldRepoUrl(originRepo: Repository, botUser: User): String {
        return originRepo.url.replace(originRepo.user.login, botUser.login)
    }

    fun getReleasesUrl(repository: Repository): String {
        return repository.releasesUrl
            .removeSuffix("{/id}")
            .removeSuffix("/")
    }

    fun getReadMeUrl(repository: Repository): String {
        return repository.url
            .removeSuffix("/")
            .plus("/readme")
    }

    fun getPullsUrl(repository: Repository): String {
        return repository.url + pullsSuffix
    }
}
