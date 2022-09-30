package ru.isb.bot.utils;

import ru.isb.bot.dto.ScheduleDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class MessageUtils {

    public static String formatMessage(List<ScheduleDTO> scheduleDTOs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Расписание занятий!</b>").append("\n");
        Map<Long, List<ScheduleDTO>> mapSchedules = parseDTO(scheduleDTOs);

        mapSchedules.forEach((k,v) -> {
            stringBuilder.append("<b>").append(mapSchedules.get(k).get(0).getWeekday_name()).append(" ").append(mapSchedules.get(k).get(0).getDate_start_text()).append("</b>").append("\n");
            v.forEach(value -> {
                stringBuilder.append("<pre><b>").append(DateUtils.calcTimeDiscipline(value.getDaytime_name())).append(" ").append("</b></pre>");
                stringBuilder.append("<b>").append(value.getCabinet_fullnumber_wotype()).append("</b>").append(" ");
                stringBuilder.append(value.getDiscipline_name()).append(" ");
                stringBuilder.append("(").append(value.getTeacher_fio()).append(") ");
                stringBuilder.append("\n");
            });
            stringBuilder.append("\n");
        });

        return stringBuilder.toString();
    }

    private static Map<Long, List<ScheduleDTO>> parseDTO(List<ScheduleDTO> scheduleDTOs) {
        Map<Long, List<ScheduleDTO>> resultDate = new TreeMap<>();

        scheduleDTOs.forEach(scheduleDTO -> {;
            if (resultDate.containsKey(scheduleDTO.getDate_start().getTime())) {
                resultDate.get(scheduleDTO.getDate_start().getTime()).add(scheduleDTO);;
            } else {
                List<ScheduleDTO> list = new ArrayList<>();
                list.add(scheduleDTO);
                resultDate.put(scheduleDTO.getDate_start().getTime(), list);
            }
        });
        resultDate.forEach((k,v) -> {
            v.sort(Comparator.comparing(ScheduleDTO::getDaytime_name));
        });
        return resultDate;
    }
}
