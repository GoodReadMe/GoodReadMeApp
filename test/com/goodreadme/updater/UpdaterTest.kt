package com.goodreadme.updater

import com.goodreadme.NothingToUpdateException
import io.kotlintest.shouldBe
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertFailsWith

internal class UpdaterTest {

    private val updater = Updater()

    private val versionsList = listOf(
        Versions("0.1", "0.2"),
        Versions("0.1", "2.0"),
        Versions("0.1.9", "1.2.3"),
        Versions("0.1", "9.0"),
        Versions("v1", "v9.0"),
        Versions("v3", "v.9.0"),
        Versions("v3", "v5"),
        Versions("0.0.5-alfa", "0.0.6-alfa"),
        Versions("0.0.5-alfa", "0.0.5"),
        Versions("0.0.5-alfa", "0.0.6"),
        Versions("1.0.5-alfa", "1.0.5-beta"),
        Versions("2.0.0-alfa", "2.1.0"),
        Versions("2.0.19", "2.18")
    )

    private val readMeTemplates = listOf(
        //Simple template
        """
            |# Codegen ktx
            |Add the dependency
            |```groovy
            |implementation 'com.github.parimatchtech:codegen-ktx:$VERSION_PATTERN'
            |```
        """.trimMargin(),
        //Template with svg badge some text and headers
        """
            |# Codegen ktx
            |[![](https://jitpack.io/v/parimatchtech/codegen-ktx.svg)](https://jitpack.io/#parimatchtech/codegen-ktx)
            |
            |We copy this class into all the little internal libraries we make. We are tired of doing it. Now it's a library.
            |
            |## Integration
            |Add it in your root build.gradle at the end of repositories:
            |```groovy
            |allprojects {
            |    repositories {
            |        ...
            |        maven { url 'https://jitpack.io' }
            |    }
            |}
            |```
            |Add the dependency
            |```groovy
            |implementation 'com.github.parimatchtech:codegen-ktx:$VERSION_PATTERN'
            |```
            |
            |## Docs
            |#### Element
            |- get annotation mirrors by KClass `Element.getAnnotationMirror(Nullable::class)`
            |- get annotation mirrors by Name `Element.getAnnotationMirror(Nullable::class)`
            |- `Element.asTypeElement`
            |- `Element.asDeclaredType()` 
        """.trimMargin(),
        //ReadMe with badge two dependencies, another code blocks and image
        """
            |# RemarkAndroidSdk
            |![CI](https://github.com/jordan1997/RemarkAndroidSdk/workflows/CI/badge.svg)
            |[![](https://jitpack.io/v/jordan1997/RemarkAndroidSdk.svg)](https://jitpack.io/#jordan1997/RemarkAndroidSdk)
            |[![codecov](https://codecov.io/gh/jordan1997/RemarkAndroidSdk/branch/master/graph/badge.svg)](https://codecov.io/gh/jordan1997/RemarkAndroidSdk)
            |
            |Kotlin sdk for comment system [Remark42](https://github.com/umputun/remark42).
            |
            |## Remark-api
            |The module provides methods for work with remark rest api.  
            |```groovy
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-api:$VERSION_PATTERN'
            |```
            |
            |[Documentation](remark-api/dokka/remark-api/com.example.remark_api/-remark-api/index.md)
            |
            |### How to set up
            |```kotlin
            |val remarkSite = RemarkSiteData("<BaseUrl>", "<SiteId>")
            |val remarkApi = RemarkApi(remarkSite, applicationContext)
            |```
            |
            |## Remark-UI
            |The module provides an android ui component for remark.
            |```groovy
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-ui:$VERSION_PATTERN'
            |```
            |
            |### How to set up
            |Create a fragment
            |```kotlin
            |CommentFragment.newInstance(
            |                    RemarkSiteData("<base url>", "<site-id>"),
            |                    "<post url>"
            |                )
            |```
            |Sample integration with Remark demo in [app](https://github.com/jordan1997/RemarkAndroidSdk/tree/master/app)
            |
            |![](https://raw.githubusercontent.com/jordan1997/RemarkAndroidSdk/master/images/image.png)
        """.trimMargin(),
        //Simple ReadMe only with shell script/command line
        """
            |```shell script
            |docker pull vovochkastelmashchuk/good-readme:$VERSION_PATTERN
            |docker run -p 8080:8080 --env GIT_HUB_TOKEN=<Github token> --rm vovochkastelmashchuk/good-readme:$VERSION_PATTERN
            |```
        """.trimMargin(),
        //ReadMe with version inside shell script/command line
        """
            |# GoodReadMe
            |The app update version of library in your ReadMe file.
            |
            |## How it's work
            |GitHub send webhook about new release -> App fork your repo -> App create pull request with change in your ReadMe file.
            |
            |## Setup for public repo
            |Add webhook to your repo:
            |`http://159.203.181.74:8080/webhook/github`
            |
            |## Setup app for self host usage.
            |### Easy run (DockerHub)
            |```shell script
            |docker pull vovochkastelmashchuk/good-readme:$VERSION_PATTERN
            |docker run -p 8080:8080 --env GIT_HUB_TOKEN=<Github token> --rm vovochkastelmashchuk/good-readme:$VERSION_PATTERN
            |```
            |
            |### Run from source code with docker
            |Change github.token in [application.conf](resources/application.conf)
            |Build jar file
            |```shell script
            |./gradlew shadowJar 
            |```
            |Build docker image
            |```shell script
            |docker build --tag good-readme .
            |```
            |Run docker image
            |```shell script
            |docker run -p 8080:8080 --env GIT_HUB_TOKEN=<Your github token> --rm good-readme
            |```
            |
            |### Run from source code
            |Change github.token in [application.conf](resources/application.conf)
            |Build and run jar file
            |```shell script
            |./gradlew shadowJar && java -jar /build/libs/updatereadme.jar 
            |```
        """.trimMargin()
    )

    @TestFactory
    fun testUpdateCorrectReadMe(): List<DynamicTest> {
        return readMeTemplates.flatMap { readMeTemplate ->
            versionsList.map { versions ->
                DynamicTest.dynamicTest("Test") {
                    val testData = buildReadMe(readMeTemplate, versions)
                    updater.updateReadMe(testData.input, versions) shouldBe testData.expected
                }
            }
        }
    }

    @TestFactory
    fun throwErrorForNotValidReadMe(): List<DynamicTest> {
        return readMeTemplates.map { readMeTemplate ->

            DynamicTest.dynamicTest("Test") {
                val testData = buildReadMe(readMeTemplate, Versions("0.2", "0.2"))

                assertFailsWith<NothingToUpdateException> {
                    updater.updateReadMe(testData.input, Versions("0.3", "0.4"))
                }
            }
        }
    }

    private data class TestData(
        val input: String,
        val expected: String
    )

    private fun buildReadMe(template: String, versions: Versions): TestData {
        return TestData(
            template.replace(VERSION_PATTERN, versions.old),
            template.replace(VERSION_PATTERN, versions.new)
        )
    }

    companion object {
        private const val VERSION_PATTERN = "{{VERSION}}"
    }
}
