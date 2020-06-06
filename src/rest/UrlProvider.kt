package com.vova.rest

import com.vova.entities.Repository

object UrlProvider {

    private const val pullsSuffix = "/pulls"

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
