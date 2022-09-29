package ru.isb.bot.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService{

    @Autowired
    StudyScheduleClient client;



    @Override
    public String getJsonClient() throws IOException, InterruptedException {
        Response response = client.getTimetableOfClasses(new Date(2022,9,26), new Date(2022,10,6));
        if (response.body() != null) {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();

            Gson gson = builder.create();
            List<ScheduleDTO> schedules = gson.fromJson(response.body().string(), new TypeToken<ArrayList<ScheduleDTO>>() {}.getType());

            return MessageUtils.formatMessage(schedules);
        }
        return null;
    }
}
