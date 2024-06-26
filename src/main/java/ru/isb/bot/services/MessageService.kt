package ru.isb.bot.services

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chatmember.*
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMemberCount
import ru.isb.bot.clients.ChatGPTClient
import ru.isb.bot.clients.NextcloudClient
import ru.isb.bot.clients.StudyScheduleClient
import ru.isb.bot.dto.ScheduleDTO
import ru.isb.bot.utils.MessageUtils.Companion.formatMessage
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.util.stream.Collectors
import java.util.stream.IntStream


@Service
class MessageService(
        private val studyScheduleClient: StudyScheduleClient,
        private val gptClient: ChatGPTClient,
        private val nextcloudClient: NextcloudClient
) : Logging {

    companion object {
        val MESSAGE_GPT_SPLIT = "GPT -> "
    }

    private val list: Map<String, Boolean> = mutableMapOf(
            Pair("Андреев Р.Н.", false),
            Pair("Салимова П.В.", false),
            Pair("Кетов А.А.", false),
            Pair("Колышкин Ю.А.", false),
            Pair("Кузаев Р.Р.", true),
            Pair("Маркин К.А.", true),
            Pair("Сидякин И.О.", false),
            Pair("Тебеньков В.А.", true),
            Pair("Забрудский А.В.", true)
    )

    @Throws(IOException::class, InterruptedException::class)
    fun getSchedulesWeek(): String {
        val date = LocalDate.now()
        val endDate = LocalDate.now().plusDays(31)
        val response = studyScheduleClient.getTimetableOfClasses(date, endDate)
        if (response.isSuccessful) {
            val gson = GsonBuilder()
                    .setPrettyPrinting()
                    .create()

            return try {
                val schedules = gson.fromJson<List<ScheduleDTO>>(response.body?.string(),
                        object : TypeToken<List<ScheduleDTO>>() {}.type
                )

                formatMessage(schedules)
            } catch (e: JsonSyntaxException) {
                "Расписание нет! Разработчик не виновен, бейте палкой того кто составляет расписание :D"
            }

        } else {
            return """
                     ***Ошибка на стороне сервера ПГАТУ!***
                     URL: ${StudyScheduleClient.URL}
                     Код ошибки: ${response.code}
                 """.trimIndent() +
                    (if (response.body != null) """
                                             Текст ошибки: ${response.body?.string()}
                                            """.trimIndent()
                    else null) +
                    "\n***Необходимо написать Мелехину Максиму Игоревичу на учебном портале.***"
        }
    }

    fun getListGroup(): String {
        val gosTrue = list.entries.filter { entry -> entry.value }.map { entry -> entry.key }.sorted()
        val gosFalse = list.entries.filter { entry -> !entry.value }.map { entry -> entry.key }.sorted()

        return "***21.06.24/02.07.24 в 9-00 ебут по списку:***\n" +
                IntStream.range(0, gosTrue.size)
                        .mapToObj { i: Int ->
                            (i + 1).toString() + ") " + gosTrue[i] + "\n"
                        }.collect(Collectors.joining()) +
                "\nРосфинмониторинг включил международное общественное движение ЛГБТ (признано экстремистским и запрещено в России) в перечень террористов и экстремистов."
    }

    fun getAnswerGPTMessage(message: String): String {
        return gptClient.getAnswerGPT(
                message
        )
    }

    fun sendFileToNextcloud(file: File, fileName: String) {
        nextcloudClient.uploadFile(file, fileName)
    }
}