package ru.isb.bot.utils

class PathFileUtils {

    companion object {

        private val COPY_POSTFIX = "(копия)"

        fun copyFileName(fileName: String): String {
            val prefixAndPostfix = fileName.split('.')
            return if (prefixAndPostfix.size == 2) prefixAndPostfix[0] + COPY_POSTFIX + "." + prefixAndPostfix[1] else fileName + COPY_POSTFIX
        }

        fun replaceSpace(fileName: String): String =
            fileName.replace(" ", "_")

    }

}