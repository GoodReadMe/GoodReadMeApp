package com.goodreadme.entities.github

data class FileUpdateRequest(
    val message: String = COMMIT_MSG,
    val sha: String,
    val content: String
)
