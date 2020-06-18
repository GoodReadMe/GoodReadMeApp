package com.goodreadme.updater

import com.goodreadme.CannotFoundTwoCorrectRelease
import com.goodreadme.entities.github.Release

data class Versions(
    val old: String,
    val new: String
)

class VersionFinder {

    fun findVersions(releases: List<Release>): Versions {
        try {
            val new = releases.firstExclude()
            val old = releases.firstExclude(new)
            return Versions(old, new)
        } catch (e: Exception) {
            throw CannotFoundTwoCorrectRelease()
        }
    }

    private fun List<Release>.firstExclude(exclude: String? = null): String {
        return this.first { !it.draft && !it.prerelease && (exclude == null || (it.name != exclude)) }.name
    }
}
