package ru.isb.bot.clients

import lombok.SneakyThrows
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
import kotlin.math.log

@Component
class ChatGPTClientImpl : ChatGPTClient, Logging {

    @Value("\${gpt.key}")
    private val apiKey: String = ""

    @SneakyThrows
    override fun getAnswerGPT(message: String): String {
        val httpClient = HttpClient.newHttpClient()
        val chatGPTSenderDTO = ChatGPTSenderDTO()
        chatGPTSenderDTO.setContent(message)

        val json = JsonUtils.parseObjectToString(chatGPTSenderDTO)
        logger.info(json)
        // Создать HttpRequest с методом POST и установить заголовки
        val request = HttpRequest.newBuilder()
            .uri(URI("http://127.0.0.1:1337/v1/chat/completions"))
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .header("Authorization", "Bearer $apiKey").header("Content-Type", "application/json")
            .timeout(Duration.ofMinutes(3)).build()

        // Отправить запрос и получить ответ
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
        return if (response.statusCode() in 200..299) {
            try {
                val dto = JsonUtils.parseStringJsonToObject(
                    response.body(), ChatGPTReceiptDTO::class.java
                )

                dto?.choices?.let { it[0].message?.content }
                        ?: "ChatGPT отправил не известный объект. ${response.body()}"
            } catch (e: Exception) {
                throw RuntimeException("Ошибка парсинга body от ChatGPT: " + e.message)
            }
        } else {
            "Код ошибки: ${response.statusCode()}\n" +
            "Текст ошибки: ${response.body()}"
        }
    }

}