package com.vova

import com.vova.entities.Repository

class CannotFoundTwoCorrectRelease : RuntimeException("Cannot found two published releases")
class CannotCreatePullRequest(val originRepo: Repository) : RuntimeException("Cannot create pull request")
