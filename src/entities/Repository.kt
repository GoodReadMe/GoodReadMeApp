package com.vova.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Repository(
    val owner: String,
    val repo: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class FullNameRequest(
    val fullName: String
)
