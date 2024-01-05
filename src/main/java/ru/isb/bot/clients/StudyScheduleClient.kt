package ru.isb.bot.clients

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDate
import java.util.concurrent.TimeUnit

@Component
class StudyScheduleClient : Logging {

    companion object {
        val URL : String = "https://pgsha.ru/sys/shedule/getsheduleclasseszo"
    }

    private val TEXT: MediaType = "application/x-www-form-urlencoded; charset=UTF-8".toMediaType()

    @Value("\${bot.stream.id}")
    private val STREAM_ID: String = ""

    fun getTimetableOfClasses(startDate: LocalDate, endDate: LocalDate): Response {
        val client: OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(URL)
            .post("stream_id=$STREAM_ID&term=2&date_start=$startDate&date_end=$endDate".toRequestBody(TEXT))
            .build()

        return try {
            client.newCall(request).execute()
        } catch (e: IOException) {
            throw RuntimeException("Ошибка получения расписания", e)
        }
    }

}