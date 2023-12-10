package ru.isb.bot.clients

interface ChatGPTClient {
    fun getAnswerGPT(message: String): String

}