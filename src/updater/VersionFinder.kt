package com.vova.updater

import com.vova.entities.Release

data class Versions(
    val old: String,
    val new: String
)

class VersionFinder {

    fun findVersions(releases: List<Release>): Versions {
        val new = releases.firstExclude()
        val old = releases.firstExclude(new)
        return Versions(old, new)
    }

    private fun List<Release>.firstExclude(exclude: String? = null): String {
        return this.first { !it.draft && !it.prerelease && (exclude == null || (it.name != exclude)) }.name
    }
}