package ru.isb.bot.utils;

import lombok.extern.log4j.Log4j2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class DateUtils {

    private static final Integer PAIR_DURATION = 95;
    private static final String REGEXP = "[0-9][0-9]\\.[0-9][0-9]\\.[0-9][0-9][0-9][0-9]";

    public static String dateFormatDDMMYYYY(Date date) {
        return String.format("%s.%s.%s",
                String.valueOf(date.getDay()).length() < 2 ? "0" + date.getDay() : date.getDay(),
                String.valueOf(date.getMonth()).length() < 2 ? "0" + date.getMonth() : date.getMonth(),
                date.getYear());
    }

    public static String calcTimeDiscipline(String startTime) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm"); //df.parse(startTime)
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.setTime(df.parse(startTime));
            Date startDate = cal.getTime();

            cal.add(Calendar.MINUTE, PAIR_DURATION);
            Date endDate = cal.getTime();

            return String.format("c %s:%s по %s:%s",
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
