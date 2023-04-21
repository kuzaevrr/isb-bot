package ru.isb.bot.utils;

import ru.isb.bot.dto.ScheduleDTO;

import java.util.*;

public class MessageUtils {

    public static String formatMessage(List<ScheduleDTO> scheduleDTOs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Расписание занятий!</b>").append("\n");
        Map<Long, List<ScheduleDTO>> mapSchedules = parseDTO(scheduleDTOs);
        System.out.println(mapSchedules.size());
        if (mapSchedules != null && !mapSchedules.isEmpty()) {
            mapSchedules.forEach((k, v) -> {
                stringBuilder.append("<b>").append(mapSchedules.get(k).get(0).getWeekday_name()).append(" ").append(mapSchedules.get(k).get(0).getDate_start_text()).append("</b>").append("\n");
                v.forEach(value -> {
                    stringBuilder.append("<pre><b>").append(DateUtils.calcTimeDiscipline(value.getDaytime_name())).append(" ").append("</b></pre>");
                    stringBuilder.append("<b>").append(value.getCabinet_fullnumber_wotype()).append("</b>").append(" ");
                    stringBuilder.append(value.getDiscipline_name()).append(" ");
                    if (value.getTeacher_fio() != null) {
                        stringBuilder.append("(").append(value.getTeacher_fio()).append(") ");
                    }
                    stringBuilder.append("(").append(value.getNotes() != null && !value.getNotes().equals(".") ? value.getNotes() + "!" : value.getClasstype_short()).append(")");
                    stringBuilder.append("\n");
                });
                stringBuilder.append("\n");
            });
        } else {
            stringBuilder.append("Расписание нет! ").append("Разработчик не виновен, бейте палкой того кто составляет расписание :D");
        }
        return stringBuilder.toString();
    }

    private static Map<Long, List<ScheduleDTO>> parseDTO(List<ScheduleDTO> scheduleDTOs) {
        Map<Long, List<ScheduleDTO>> resultDate = new TreeMap<>();

        if (scheduleDTOs != null) {
            scheduleDTOs.forEach(scheduleDTO -> {
                ;
                if (resultDate.containsKey(scheduleDTO.getDate_start().getTime())) {
                    resultDate.get(scheduleDTO.getDate_start().getTime()).add(scheduleDTO);
                    ;
                } else {
                    List<ScheduleDTO> list = new ArrayList<>();
                    list.add(scheduleDTO);
                    resultDate.put(scheduleDTO.getDate_start().getTime(), list);
                }
            });
            resultDate.forEach((k, v) -> {
                v.sort(Comparator.comparing(scheduleDTO -> Integer.parseInt(scheduleDTO.getDaytime_name().split(":")[0])));
            });
        }
        return resultDate;
    }
}
