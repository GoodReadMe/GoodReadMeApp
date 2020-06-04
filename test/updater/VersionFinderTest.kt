package updater

import com.vova.entities.Release
import com.vova.updater.VersionFinder
import com.vova.updater.Versions
import io.kotlintest.shouldBe
import kotlin.test.Test

internal class VersionFinderTest {

    private val versionFinder = VersionFinder()

    @Test
    fun simpleTest() {
        val newName = "newName"
        val oldName = "oldName"

        val newRelease = Release(newName, false, false)
        val oldRelease = Release(oldName, false, false)
        val releases = listOf(newRelease, oldRelease)
        versionFinder.findVersions(releases) shouldBe Versions(oldName, newName)
    }
}