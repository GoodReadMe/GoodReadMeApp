package com.vova.rest

import com.vova.entities.Repository
import okhttp3.HttpUrl

object UrlProvider {

    private const val pullsSuffix = "/pulls"

    fun getReleasesUrl(repository: Repository): String {
        return repository.releasesUrl
            .removeSuffix("{/id}")
            .removeSuffix("/")
    }

    fun getReadMeUrl(repository: Repository): String {
        return HttpUrl.get(repository.url)
            .newBuilder()
            .addPathSegment("readme")
            .build()
            .toString()
    }

    fun getPullsUrl(repository: Repository): String {
        return repository.url + pullsSuffix
    }
}
