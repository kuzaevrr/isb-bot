package ru.isb.bot.clients;

import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.isb.bot.utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class StudyScheduleClientImpl implements StudyScheduleClient {

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    public static final MediaType TEXT = MediaType.get("application/x-www-form-urlencoded; charset=UTF-8");
    @Value("${bot.stream.id}")
    private String STREAM_ID;

    @Override
    public Response getTimetableOfClasses(LocalDate startDate, LocalDate endDate) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        RequestBody requestBody = RequestBody.create(TEXT,
                                                     String.format("stream_id=%s&term=2&date_start=%s&date_end=%s",
                                                             STREAM_ID,
                                                             startDate,
                                                             endDate
                                                     ));
        Request request = new Request.Builder()
                .url("https://pgsha.ru/sys/shedule/getsheduleclasseszo")
                .post(requestBody)
                .build();

        log.info(String.format("stream_id=%s&term=2&date_start=%s&date_end=%s",
                STREAM_ID,
                startDate,
                endDate));
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка получения расписания", e);
        }
    }
}
