package ru.isb.bot.services;

import java.io.IOException;

public interface MessageService {

    String getSchedulesWeek() throws IOException, InterruptedException, ClassNotFoundException;
}
