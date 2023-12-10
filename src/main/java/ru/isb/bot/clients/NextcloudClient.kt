package ru.isb.bot.clients

import java.io.File

interface NextcloudClient {

    fun uploadFile(file: File, fileName: String)

}