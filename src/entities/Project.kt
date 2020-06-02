package com.vova.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProjectReadMe(
    val path: String,
    val sha: String,
    val content: String
)