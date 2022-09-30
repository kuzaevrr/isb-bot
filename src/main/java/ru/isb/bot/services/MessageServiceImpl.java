package ru.isb.bot.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
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
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    StudyScheduleClient client;



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
}
