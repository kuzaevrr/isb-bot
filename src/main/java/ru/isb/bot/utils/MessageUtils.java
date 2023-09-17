package ru.isb.bot.utils;

import ru.isb.bot.dto.ScheduleDTO;

import java.util.*;
import java.util.stream.Stream;

public class MessageUtils {

    public static String formatMessage(List<ScheduleDTO> scheduleDTOs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<b>Расписание занятий!</b>").append("\n");
        Map<Long, List<ScheduleDTO>> mapSchedules = parseDTO(scheduleDTOs);

        if (!mapSchedules.isEmpty()) {
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
                if (resultDate.containsKey(scheduleDTO.getDate_start().getTime())) {
                    resultDate.get(scheduleDTO.getDate_start().getTime()).add(scheduleDTO);
                } else {
                    List<ScheduleDTO> list = new ArrayList<>();
                    list.add(scheduleDTO);
                    resultDate.put(scheduleDTO.getDate_start().getTime(), list);
                }
            });
            resultDate.forEach((k, v) -> v.sort(
                    Comparator.comparing(scheduleDTO -> Integer.parseInt(scheduleDTO.getDaytime_name().split(":")[0])))
            );
        }
        return resultDate;
    }

    public static List<String> textSplitter(String text) {
        int maxLength = 4096;

        if (text.length() < maxLength) {
            return Collections.singletonList(text);
        }

        List<String> words = Stream.of(text.split("\\s+"))
                .toList();

        List<String> result = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        words.forEach(word -> {
            if (currentLine.length() + word.length() + 1 <= maxLength) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                result.add(currentLine.toString());
                currentLine.setLength(0);
                currentLine.append(word);
            }
        });

        // Добавьте последнюю строку (если есть)
        if (!currentLine.isEmpty()) {
            result.add(currentLine.toString());
        }

        return result;
    }
}
