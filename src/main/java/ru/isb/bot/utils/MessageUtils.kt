package ru.isb.bot.utils

import org.apache.logging.log4j.kotlin.Logging
import ru.isb.bot.dto.ScheduleDTO
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

class MessageUtils : Logging {

    companion object {

        private val regex =
            setOf("\\_", "\\*", "[", "]", "(", ")", "~", "`", ">", "#", "\\+", "-", "=", "\\|", "{", "}", "\\.", "\\!")

        private val originalSet =
            setOf("_", "*", "+", "|", ".", "!")

        fun formatMessage(scheduleDTOs: List<ScheduleDTO>?): String {
            var message = "***Расписание занятий!***\n"
            val mapSchedules = parseDTO(scheduleDTOs)
            if (mapSchedules.isNotEmpty()) {
                mapSchedules.forEach { (k: Long, v: List<ScheduleDTO>) ->
                    message += "***${mapSchedules[k]?.get(0)?.weekday_name} ${mapSchedules[k]?.get(0)?.date_start_text}***\n"
                    v.forEach(Consumer { value: ScheduleDTO ->
                        message += ("***${DateUtils.calcTimeDiscipline(value.daytime_name)}***\n" +
                                "***${value.cabinet_fullnumber_wotype}*** " +
                                "${value.discipline_name} " +
                                value.teacher_fio?.let { "(${it}) " }.orEmpty() +
                                "(${(if (value.notes?.let { it != "." } == true) value.notes + "!" else value.classtype_short)})\n")
                    })
                    message += "\n"
                }
            } else {
                message += "Расписание нет! Разработчик не виновен, бейте палкой того кто составляет расписание :D"
            }
            return message
        }

        private fun parseDTO(scheduleDTOs: List<ScheduleDTO>?): Map<Long, MutableList<ScheduleDTO>> {
            val resultDate: MutableMap<Long, MutableList<ScheduleDTO>> = mutableMapOf()
            scheduleDTOs?.let {
                it.forEach(Consumer { scheduleDTO: ScheduleDTO ->
                    if (resultDate.containsKey(scheduleDTO.date_start?.time)) {
                        resultDate[scheduleDTO.date_start?.time]?.add(scheduleDTO)
                    } else {
                        val list: MutableList<ScheduleDTO> = ArrayList()
                        list.add(scheduleDTO)
                        scheduleDTO.date_start?.time?.let { it1 -> resultDate.put(it1, list) }
                    }
                })
                resultDate.forEach { (_: Long?, v: List<ScheduleDTO>) ->
                    v.sortWith(
                        Comparator.comparing { scheduleDTO: ScheduleDTO ->
                            scheduleDTO.daytime_name?.split(":")?.get(0)?.toInt() ?: 0
                        }
                    )
                }

            }

            return resultDate
        }

        fun textSplitter(text: String): List<String> {
            val maxLength = 4090
            if (text.length < maxLength) {
                return listOf(text)
            }
            val words = text.split(' ');

            val result: MutableList<String> = mutableListOf()
            var currentLine = ""
            words.forEach { word: String ->
                if (currentLine.length + word.length + 1 < maxLength) {
                    if (currentLine.isNotEmpty()) {
                        currentLine += " "
                    }
                    currentLine += word
                } else {
                    result.add(addEndString4090(currentLine))
                    currentLine = word
                }
            }

            // Добавьте последнюю строку (если есть)
            if (currentLine.isNotEmpty()) {
                result.add(addStartString4090(currentLine))
            }
            return result
        }

        private fun addEndString4090(input: String): String {
            regex.forEach { regexText ->
                val regex = Regex(regexText)
                if (unclosedSymbols(input, regex)) {
                    return "${input}${regex.pattern}"
                }
            }
            return input;
        }

        private fun addStartString4090(input: String): String {
            regex.forEach { regexText ->
                val regex = Regex(regexText)
                if (unclosedSymbols(input, regex)) {
                    return "${regex.pattern}${input}"
                }
            }
            return input;
        }

        private fun unclosedSymbols(input: String, regex: Regex): Boolean =
            regex.findAll(input)
                .count() % 2 != 0

        fun concatInputMessage(message: String): String {
            originalSet.forEach { regexText ->
                message.replace(regexText, "\\" + regexText)
            }
            return message;
        }
    }

}