package com.vova.entities

data class PRRequest(
    val title: String = COMMIT_MSG,
    val body: String = COMMIT_MSG,
    val head: String,
    val base: String
)