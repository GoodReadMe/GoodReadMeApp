package com.vova

import com.vova.entities.github.GitHubRepository

class CannotFoundTwoCorrectRelease : RuntimeException("Cannot found two published releases")
class CannotCreatePullRequest(val originRepo: GitHubRepository) : RuntimeException("Cannot create pull request")
class NothingToUpdate : RuntimeException("Nothing to update")
