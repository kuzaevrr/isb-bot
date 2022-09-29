package ru.isb.bot.clients;

import okhttp3.Response;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.Date;

public interface StudyScheduleClient {

    Response getTimetableOfClasses(Date dateStart, Date dateEnd) throws IOException, InterruptedException;
}
