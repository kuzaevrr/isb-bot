package ru.isb.bot.utils

class StringUtils {

    companion object {
        fun replaceHTML(text: String): String =
            text.replace("<".toRegex(), "&lt;")
                .replace(">".toRegex(), "&gt;")
                .replace("!".toRegex(), "&#33;")
                .replaceFirst("```".toRegex(), "<pre>")
                .replaceFirst("```".toRegex(), "</pre>")

    }
}