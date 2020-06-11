package com.vova.entities.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubReleaseHook(
    val action: String,
    val release: Release,
    val repository: GitHubRepository
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Release(
    val name: String,
    val draft: Boolean,
    val prerelease: Boolean
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubRepository(
    @JsonProperty("default_branch")
    val defaultBranch: String,
    @JsonProperty("full_name")
    val fullName: String,
    val name: String,
    @JsonProperty("releases_url")
    val releasesUrl: String,
    @JsonProperty("forks_url")
    val forkUrl: String,
    val url: String,
    @JsonProperty("owner")
    val user: User
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    val login: String
)
