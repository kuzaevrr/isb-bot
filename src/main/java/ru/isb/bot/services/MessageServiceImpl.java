package ru.isb.bot.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.isb.bot.clients.StudyScheduleClient;
import ru.isb.bot.dto.ScheduleDTO;
import ru.isb.bot.utils.MessageUtils;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final StudyScheduleClient client;

    private List<String> list = Arrays.asList("Андреев Р.Н.",
            "Буркова П.В.",
            "Кетов А.А.",
            "Колышкин Ю.А.",
            "Кузаев Р.Р.",
            "Маркин К.А.",
            "Сидякин И.О.",
            "Тебеньков В.А.",
            "Забрудский А.В.");

    @Override
    public String getSchedulesWeek() throws IOException, InterruptedException {
        LocalDate date = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(10);
        Response response = client.getTimetableOfClasses(date, endDate);
        if (response.body() != null) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            List<ScheduleDTO> schedules = gson.fromJson(response.body().string(), new TypeToken<ArrayList<ScheduleDTO>>() {}.getType());
            return MessageUtils.formatMessage(schedules);
        }
        return null;
    }

    @Override
    public String getListGroup() {
        list.sort(String::compareTo);
        return "<b>Список группы: </b>\n" + IntStream.range(0, list.size())
                .mapToObj(i -> i + 1 + ") " + list.get(i) + "\n").collect(Collectors.joining());
    }
}
