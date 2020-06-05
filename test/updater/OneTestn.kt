package updater

import com.vova.updater.Updater
import com.vova.updater.Versions
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test

class OneTestn {

    @Test
    fun test() {
        val input = """
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
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-api:v1'
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
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-ui:v1'
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
        """.trimMargin()

        val expected = """
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
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-api:v9.0'
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
            |implementation 'com.github.jordan1997.RemarkAndroidSdk:remark-ui:v9.0'
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
        """.trimMargin()

        Updater().updateReadMe(input, Versions("v1", "v9.0")) shouldBe expected
    }
}