package com.vova.updater

import java.util.*

class Updater {

    fun updateReadMe(content: String): String {
        return "$content add new line from bot"
    }
}

class Base64Updater(private val updater: Updater = Updater()) {

    fun updateReadMeBase64(base64: String): String {
        val content = String(Base64.getMimeDecoder().decode(base64))
        return Base64.getEncoder().encodeToString(updater.updateReadMe(content).toByteArray())
    }
}