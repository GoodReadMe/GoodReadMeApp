package com.goodreadme.entities.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectReadMe(
    val url: String,
    val path: String,
    val sha: String,
    val content: String
)
