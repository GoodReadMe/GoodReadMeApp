package com.goodreadme.updater

import com.goodreadme.NothingToUpdateException
import java.util.*

class Updater {

    companion object {
        private const val CODE_MARKDOWN_MARKET = "```"
    }

    fun updateReadMe(content: String, versions: Versions): String {
        var result = content

        fun tryReturnResult(): String {
            if (result == content) {
                throw NothingToUpdateException()
            }
            return result
        }

        var i = 0
        while (i < result.length) {
            val start = result.indexOf(CODE_MARKDOWN_MARKET, startIndex = i)
            if (start !in result.indices) {
                return tryReturnResult()
            }
            val end = result.indexOf(CODE_MARKDOWN_MARKET, startIndex = start + CODE_MARKDOWN_MARKET.length)
            if (end !in result.indices) {
                return tryReturnResult()
            }
            val newString = result.substring(start, end).replace(versions.old, versions.new)
            result = result.replaceRange(start, end, newString)
            i = end
        }

        return tryReturnResult()
    }
}

class Base64Updater(private val updater: Updater = Updater()) {

    fun updateReadMeBase64(base64: String, versions: Versions): String {
        val content = String(Base64.getMimeDecoder().decode(base64))
        return Base64.getEncoder().encodeToString(updater.updateReadMe(content, versions).toByteArray())
    }
}
