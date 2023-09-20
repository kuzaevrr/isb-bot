package ru.isb.bot.services;

import java.io.File;
import java.io.IOException;

public interface MessageService {

    String getSchedulesWeek() throws IOException, InterruptedException, ClassNotFoundException;
    String getListGroup();
    String getAnswerMessage(String text);
    void sendFileToNextcloud(File file, String fileName);

}
