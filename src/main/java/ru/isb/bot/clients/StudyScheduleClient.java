package ru.isb.bot.clients;

import okhttp3.Response;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Date;

public interface StudyScheduleClient {

    Response getTimetableOfClasses(LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException;
}
