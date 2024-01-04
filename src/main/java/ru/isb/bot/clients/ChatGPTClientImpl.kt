package ru.isb.bot.clients

import com.google.gson.Gson
import lombok.SneakyThrows
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.isb.bot.dto.ChatGPTReceiptDTO
import ru.isb.bot.dto.ChatGPTSenderDTO
import ru.isb.bot.utils.JsonUtils
import java.io.IOException
import java.lang.Exception
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

        val json = JsonUtils.parseObjectToString(ChatGPTSenderDTO().setContent(message))
        val request = Request.Builder()
            .url("http://127.0.0.1:1337/v1/chat/completions")
            .post(json.toRequestBody(JSON))
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .header("Accept-Encoding", "identity")
            .build()


        // Отправить запрос и получить ответ
        val response: Response = client.newCall(request).execute()
        response.use { it ->
            if (it.isSuccessful) {
                it.body?.use { responseBody ->
                    val dto = Gson().fromJson(responseBody.string(), ChatGPTReceiptDTO::class.java)
                    return dto?.choices?.let { it[0].message?.content }
                        ?: "ChatGPT отправил не известный объект. ${responseBody.string()}"
                }
            } else {
                return "Код ошибки: ${it.code}\n" +
                        it.body?.use {
                            "Текст ошибки: ${it.string()}"
                        }
            }
        }

        return "GPT отдал пустой ответ"
    }


}