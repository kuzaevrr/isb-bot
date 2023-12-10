package ru.isb.bot.clients

import okhttp3.Response
import java.io.IOException
import java.time.LocalDate

interface StudyScheduleClient {

    @Throws(IOException::class, InterruptedException::class)
    fun getTimetableOfClasses(startDate: LocalDate, endDate: LocalDate): Response

}