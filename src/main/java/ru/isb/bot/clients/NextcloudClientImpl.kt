package ru.isb.bot.clients

import lombok.SneakyThrows
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPut
import org.apache.http.entity.ContentType
import org.apache.http.entity.FileEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import ru.isb.bot.utils.PathFileUtils.Companion.copyFileName
import ru.isb.bot.utils.PathFileUtils.Companion.replaceSpace
import java.io.File
import java.io.IOException
import java.util.*

@Component
class NextcloudClientImpl : NextcloudClient, Logging {

    @Value("\${nextcloud.host}")
    private val NEXTCLOUD_HOST: String = ""

    @Value("\${nextcloud.user.name}")
    private val NEXTCLOUD_USER_NAME: String = ""

    @Value("\${nextcloud.user.pass}")
    private val NEXTCLOUD_USER_PASS: String = ""

    @Value("\${nextcloud.path.to.file}")
    private val NEXTCLOUD_PATH_TO_FILE: String = ""
    private val NEXTCLOUD_URL_PATH = "/remote.php/dav/files/isb/"

    @SneakyThrows
    override fun uploadFile(file: File, fileName: String) {
        val replacedFileName = replaceSpace(fileName)

        try {
            HttpClients.createDefault().use { httpClient ->
                val request =
                    HttpPut("$NEXTCLOUD_HOST$NEXTCLOUD_URL_PATH$NEXTCLOUD_PATH_TO_FILE/$replacedFileName")
                val encodedCredentials = createCredentials(request)
                val fileEntity = createEntityFile(request, file)
                httpClient.execute(request).use { response ->
                    val statusLine = response.statusLine
                    val statusCode = statusLine.statusCode
                    logger.info("File upload status code: $statusCode")
                    if (statusCode == 204) {
                        uploadFile(file, copyFileName(replacedFileName))
                    } else if (statusCode == 302) {
                        redirect(response, fileEntity, encodedCredentials, httpClient)
                    }
                }
            }
        } catch (ex: IOException) {
            logger.error(ex.message ?: "Ошибка запроса к облаку", ex)
        }
    }

    @SneakyThrows
    private fun redirect(
        response: CloseableHttpResponse,
        fileEntity: FileEntity,
        encodedCredentials: String,
        httpClient: CloseableHttpClient
    ) {
        val redirectUrl = response.getFirstHeader("Location").value

        logger.info("Redirect URL: $redirectUrl")

        val redirectRequest = HttpPut(redirectUrl)
        redirectRequest.addHeader("Authorization", "Basic $encodedCredentials")
        redirectRequest.entity = fileEntity
        httpClient.execute(redirectRequest).use { redirectResponse ->
            logger.info("Redirect status code: ${redirectResponse.statusLine.statusCode}")
        }
    }

    private fun createCredentials(request: HttpPut): String {
        val credentials = "$NEXTCLOUD_USER_NAME:$NEXTCLOUD_USER_PASS"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())
        request.addHeader("Authorization", "Basic $encodedCredentials")
        return encodedCredentials
    }

    private fun createEntityFile(request: HttpPut, file: File): FileEntity {
        val fileEntity = FileEntity(file, ContentType.DEFAULT_BINARY)
        request.entity = fileEntity
        return fileEntity
    }

}