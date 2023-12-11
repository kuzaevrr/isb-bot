package ru.isb.bot.clients

import lombok.SneakyThrows
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.isb.bot.dto.ChatGPTReceiptDTO
import ru.isb.bot.dto.ChatGPTSenderDTO
import ru.isb.bot.utils.JsonUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import java.util.concurrent.TimeUnit

@Component
class ChatGPTClientImpl : ChatGPTClient, Logging {

    @Value("\${gpt.key}")
    private val apiKey: String = ""

    val JSON = "application/json; charset=utf-8".toMediaType()


    @SneakyThrows
    override fun getAnswerGPT(message: String): String {
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.MINUTES)
            .writeTimeout(3, TimeUnit.MINUTES)
            .readTimeout(3, TimeUnit.MINUTES)
            .retryOnConnectionFailure(false)
            .build()

        val chatGPTSenderDTO = ChatGPTSenderDTO()
        chatGPTSenderDTO.setContent(message)
        logger.info(message)

        val json = JsonUtils.parseObjectToString(chatGPTSenderDTO)
        logger.info(json)

        val request = Request.Builder()
            .url("http://127.0.0.1:1337/v1/chat/completions")
            .post(json.toRequestBody(JSON))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .header("Accept-Encoding", "identity")
            .build()

        // Отправить запрос и получить ответ
        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            try {
                val dto = JsonUtils.parseStringJsonToObject(
                    response.body?.string(), ChatGPTReceiptDTO::class.java
                )

                dto?.choices?.let { it[0].message?.content }
                        ?: "ChatGPT отправил не известный объект. ${response.body?.string()}"
            } catch (e: Exception) {
                throw RuntimeException("Ошибка парсинга body от ChatGPT: " + e.message)
            }
        } else {
            "Код ошибки: ${response.code}\n" +
            "Текст ошибки: ${response.body?.string()}"
        }
    }

}