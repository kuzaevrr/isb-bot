package ru.isb.bot.utils

import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import ru.isb.bot.dto.ScheduleDTO
import java.util.*
import java.util.function.Consumer
import java.util.stream.Stream

class MessageUtils : Logging {

    companion object {

        fun formatMessage(scheduleDTOs: List<ScheduleDTO>?): String {
            var message = "<b>Расписание занятий!</b>\n"
            val mapSchedules = parseDTO(scheduleDTOs)
            if (mapSchedules.isNotEmpty()) {
                mapSchedules.forEach { (k: Long, v: List<ScheduleDTO>) ->
                    message += "<b>${mapSchedules[k]?.get(0)?.weekday_name} ${mapSchedules[k]?.get(0)?.date_start_text}</b>\n"
                    v.forEach(Consumer { value: ScheduleDTO ->
                        message += ("<b>${DateUtils.calcTimeDiscipline(value.daytime_name)}</b>\n" +
                                "<b>${value.cabinet_fullnumber_wotype}</b> " +
                                "${value.discipline_name} " +
                                "${value.teacher_fio?.let { "(${it}) " }}" +
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
            val maxLength = 4096
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
                    result.add(currentLine)
                    currentLine = word
                }
            }

            // Добавьте последнюю строку (если есть)
            if (currentLine.isNotEmpty()) {
                result.add(currentLine)
            }
            return result
        }

    }

}