package ru.isb.bot.utils

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import lombok.SneakyThrows

class JsonUtils {

    companion object {
        private val mapper = ObjectMapper()

        init {
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }

        @SneakyThrows
        fun <T> parseStringJsonToObject(content: String?, clazz: Class<T>?): T? {
            return mapper.readValue(content, clazz)
        }

        @SneakyThrows
        fun parseObjectToString(`object`: Any?): String {
            return mapper.writeValueAsString(`object`)
        }
    }
}