package rest

import com.vova.entities.Repository
import com.vova.rest.UrlProvider
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

class UrlProviderTest {

    private val repoPrefix = "https://api.github.com/repos/VovaStelmashchuk/Test-repo"

    @TestFactory
    fun createReleaseUrl() = listOf(
        "$repoPrefix/releases{/id}",
        "$repoPrefix/releases/",
        "$repoPrefix/releases"
    ).map { mockUrl ->
        DynamicTest.dynamicTest("Create releases url") {
            val repo = mockk<Repository> {
                every { releasesUrl } answers { mockUrl }
            }
            UrlProvider.getReleasesUrl(repo) shouldBe "$repoPrefix/releases"
        }
    }

    @TestFactory
    fun createReadMeUrl() = listOf(
        repoPrefix,
        "$repoPrefix/"
    ).map { mockUrl ->
        DynamicTest.dynamicTest("Create readme url") {
            val repo = mockk<Repository> {
                every { url } answers { mockUrl }
            }
            UrlProvider.getReadMeUrl(repo) shouldBe "$repoPrefix/readme"
        }
    }
}
