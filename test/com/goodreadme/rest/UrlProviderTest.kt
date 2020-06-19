package com.goodreadme.rest

import com.goodreadme.entities.github.GitHubRepository
import com.goodreadme.entities.github.User
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class UrlProviderTest {

    private val repoPrefix = "https://api.github.com/repos/VovaStelmashchuk/Test-repo"

    @TestFactory
    fun createOldRepoUrl(): List<DynamicTest> {
        class TestInput(
            val originUserName: String,
            val botUserName: String,
            val originUrl: String
        )

        return listOf(
            TestInput(
                "VovaStelmashchuk",
                "Bot",
                "https://api.github.com/repos/VovaStelmashchuk/Test-repo"
            ) to "https://api.github.com/repos/Bot/Test-repo"
        ).map { (input, expected) ->
            DynamicTest.dynamicTest("Create old url") {
                val repo = mockk<GitHubRepository> {
                    every { url } answers { input.originUrl }
                    every { user } answers {
                        mockk {
                            every { login } answers { input.originUserName }
                        }
                    }
                }
                val botUser = mockk<User> {
                    every { login } answers { input.botUserName }
                }
                UrlProvider.getOldRepoUrl(repo, botUser) shouldBe expected
            }
        }
    }

    @TestFactory
    fun createReleaseUrl() = listOf(
        "$repoPrefix/releases{/id}",
        "$repoPrefix/releases/",
        "$repoPrefix/releases"
    ).map { mockUrl ->
        DynamicTest.dynamicTest("Create releases url") {
            val repo = mockk<GitHubRepository> {
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
            val repo = mockk<GitHubRepository> {
                every { url } answers { mockUrl }
            }
            UrlProvider.getReadMeUrl(repo) shouldBe "$repoPrefix/readme"
        }
    }
}
