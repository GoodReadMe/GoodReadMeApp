package com.vova.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReleaseHook(
    val release: Release,
    val repository: Repository
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
    val name: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    @JsonProperty("default_branch")
    val defaultBranch: String,
    @JsonProperty("full_name")
    val fullName: String,
    @JsonProperty("forks_url")
    val forkUrl: String,
    val url: String,
    val owner: Owner
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Owner(
    val login: String
)
