package ru.isb.bot.utils;

import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Log4j2
public class DateUtils {

    private static final Integer PAIR_DURATION = 90;

    public static String calcTimeDiscipline(String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); //df.parse(startTime)
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.setTime(df.parse(startTime));
            Date startDate = cal.getTime();

            cal.add(Calendar.MINUTE, PAIR_DURATION);
            Date endDate = cal.getTime();

            return String.format("%s:%s - %s:%s",
                    startDate.getHours(),
                    String.valueOf(startDate.getMinutes()).length() < 2 ? "0" + startDate.getMinutes() : startDate.getMinutes(),
                    endDate.getHours(),
                    String.valueOf(endDate.getMinutes()).length() < 2 ? "0" + endDate.getMinutes() : endDate.getMinutes());
        } catch (ParseException e) {
            log.error("Invalid format date", e);
            return "с **:** по **:**";
        }
    }
}
