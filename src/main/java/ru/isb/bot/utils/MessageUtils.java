package ru.isb.bot.utils;

import ru.isb.bot.dto.ScheduleDTO;

import java.util.*;

public class MessageUtils {

    public static String formatMessage(List<ScheduleDTO> scheduleDTOs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*Расписание занятий!*").append("\n");
        scheduleDTOs.sort(Comparator.comparing(ScheduleDTO::getDate_start));

        Set<Date> dates = new HashSet<>();
        for (ScheduleDTO scheduleDTO :scheduleDTOs ) {
            if (!dates.contains(scheduleDTO.getDate_start())) {
                stringBuilder.append("*").append(scheduleDTO.getWeekday_name()).append(" ").append(scheduleDTO.getDate_start_text()).append("*").append("\n");
                dates.add(scheduleDTO.getDate_start());
            }
            stringBuilder.append(DateUtils.calcTimeDiscipline(scheduleDTO.getDaytime_name())).append(" ");
            stringBuilder.append("*").append(scheduleDTO.getCabinet_fullnumber_wotype()).append("*").append(" ");
            stringBuilder.append(scheduleDTO.getDiscipline_name()).append(" ");
            stringBuilder.append("(").append(scheduleDTO.getTeacher_fio()).append(") ");
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
