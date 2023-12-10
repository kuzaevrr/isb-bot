package ru.isb.bot.services

import java.io.File
import java.io.IOException

interface MessageService {

    @Throws(IOException::class, InterruptedException::class, ClassNotFoundException::class)
    fun getSchedulesWeek(): String
    fun getListGroup(): String
    fun getAnswerGPTMessage(text: String): String
    fun sendFileToNextcloud(file: File, fileName: String)

}