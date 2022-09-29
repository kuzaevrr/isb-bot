package ru.isb.bot.services;

import ru.isb.bot.dto.ScheduleDTO;

import java.io.IOException;
import java.util.List;

public interface MessageService {

    String getJsonClient() throws IOException, InterruptedException, ClassNotFoundException;
}
