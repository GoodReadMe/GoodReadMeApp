package com.goodreadme.updater

import com.goodreadme.CannotFoundTwoCorrectRelease
import com.goodreadme.entities.github.Release
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class VersionFinderTest {

    private val versionFinder = VersionFinder()

    @Test
    fun simpleTest() {
        val newName = "newName"
        val oldName = "oldName"

        val newRelease = Release(newName, false, false)
        val oldRelease = Release(oldName, false, false)
        val releases = listOf(newRelease, oldRelease)
        versionFinder.findVersions(releases) shouldBe Versions(
            oldName,
            newName
        )
    }

    @Test
    fun oneDraftInMiddle() {
        val newName = "newName"
        val oldName = "oldName"

        val newRelease = Release(newName, false, false)
        val draftRelease = Release(newName, true, false)
        val oldRelease = Release(oldName, false, false)
        val releases = listOf(newRelease, draftRelease, oldRelease)
        versionFinder.findVersions(releases) shouldBe Versions(
            oldName,
            newName
        )
    }

    @Test
    fun oneDraftInTop() {
        val newName = "newName"
        val oldName = "oldName"

        val draftRelease = Release(newName, true, false)
        val newRelease = Release(newName, false, false)
        val oldRelease = Release(oldName, false, false)
        val releases = listOf(draftRelease, newRelease, oldRelease)
        versionFinder.findVersions(releases) shouldBe Versions(
            oldName,
            newName
        )
    }

    @Test
    fun `One draft and one prerelease in middle`() {
        val newName = "newName"
        val oldName = "oldName"

        val newRelease = Release(newName, false, false)
        val draftRelease = Release(newName, true, false)
        val preRelease = Release(newName, false, true)
        val oldRelease = Release(oldName, false, false)
        val releases = listOf(newRelease, draftRelease, preRelease, oldRelease)
        versionFinder.findVersions(releases) shouldBe Versions(
            oldName,
            newName
        )
    }

    @Test
    fun tryCatchErrorInSimpleCase() {
        val newRelease = Release("", false, false)
        val releases = listOf(newRelease)
        assertFailsWith<CannotFoundTwoCorrectRelease> {
            versionFinder.findVersions(releases)
        }
    }
}
