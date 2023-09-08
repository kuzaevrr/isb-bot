package ru.isb.bot.clients;

import java.io.File;

public interface NextcloudClient {

    void uploadFile(File file, String fileName);
}
