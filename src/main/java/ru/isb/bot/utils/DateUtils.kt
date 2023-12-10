package ru.isb.bot.utils

import org.apache.logging.log4j.kotlin.Logging
import org.apache.logging.log4j.kotlin.logger
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtils : Logging {

    companion object {
        private const val PAIR_DURATION = 90

        fun calcTimeDiscipline(startTime: String?): String {
            val df = SimpleDateFormat("HH:mm") //df.parse(startTime)
            return try {
                val cal = Calendar.getInstance(TimeZone.getDefault())

                cal.setTime(df.parse(startTime))
                val startDate = cal.time

                cal.add(Calendar.MINUTE, PAIR_DURATION)
                val endDate = cal.time
                String.format(
                    "%s:%s - %s:%s",
                    startDate.hours,
                    if (startDate.minutes.toString().length < 2) "0" + startDate.minutes else startDate.minutes,
                    endDate.hours,
                    if (endDate.minutes.toString().length < 2) "0" + endDate.minutes else endDate.minutes
                )
            } catch (e: ParseException) {
                logger.error("Invalid format date", e)
                "с **:** по **:**"
            }
        }
    }

}