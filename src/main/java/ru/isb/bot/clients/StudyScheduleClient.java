package ru.isb.bot.clients;

import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;

public interface StudyScheduleClient {

    Response getTimetableOfClasses(LocalDate startDate, LocalDate endDate) throws IOException, InterruptedException;
}
