package com.goodreadme

import com.goodreadme.entities.github.GitHubRepository

class CannotFoundTwoCorrectRelease : RuntimeException("Cannot found two published releases")
class CannotCreatePullRequest(val originRepo: GitHubRepository) : RuntimeException("Cannot create pull request")
class NothingToUpdateException : RuntimeException("Nothing to update")
