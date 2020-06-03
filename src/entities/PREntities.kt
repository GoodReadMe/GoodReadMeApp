package com.vova.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class PRRequest(
    val title: String = COMMIT_MSG,
    val body: String = COMMIT_MSG,
    val head: String,
    val base: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PRResponse(
    @JsonProperty("html_url")
    val htmlUrl: String
)