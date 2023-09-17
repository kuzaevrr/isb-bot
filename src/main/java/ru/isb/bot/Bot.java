package ru.isb.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.isb.bot.clients.ChatGPTClient;
import ru.isb.bot.clients.NextcloudClient;
import ru.isb.bot.enums.Commands;
import ru.isb.bot.services.MessageServiceImpl;

import java.io.File;

@Log4j2
@Component
@Getter
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.isb.chat.id}")
    private String ISB_CHAT_ID;

    private final MessageServiceImpl messageService;
    private final NextcloudClient nextcloudClient;
    private final ChatGPTClient gptClient;

    private static final String MESSAGE_GPT_SPLIT = "GPT -> ";

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().hasText()) {
                    switch (Commands.fromString(update.getMessage().getText())) {
                        case SCHEDULE_GROUP, SCHEDULE -> execute(
                                sendMessage(
                                        messageService.getSchedulesWeek(),
                                        update.getMessage().getChatId()
                                ));
                        case LIST_GROUP, LIST -> execute(
                                sendMessage(
                                        messageService.getListGroup(),
                                        update.getMessage().getChatId()
                                ));
                        case ALL, ALL_GROUP, ALL_LINK -> execute(
                                sendMessage(
                                        "@elektrik_gut @markin_ka @RA_prof @Mr_Ket1997 @Yureskii @Va1er1ev1ch @vladka_teb @polibuu @Desert567",
                                        update.getMessage().getChatId()
                                )
                        );
                    }
                    if (update.getMessage().getText().contains(MESSAGE_GPT_SPLIT)) {
                        String message = update.getMessage().getText().split(MESSAGE_GPT_SPLIT)[1];
                        sendMessage(
                                gptClient.getAnswerGPT(message),
                                update.getMessage().getChatId()
                        );
                    }
                } else if (update.getMessage().hasDocument()) {
                    Document document = update.getMessage().getDocument();
                    try {
                        File file = downloadFile(execute(new GetFile(document.getFileId())).getFilePath());
                        nextcloudClient.uploadFile(file, document.getFileName());
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        sendMessageException(e, update);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessageException(e, update);
        }
    }

    private SendMessage sendMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    @SneakyThrows
    private void sendMessageException(Exception e, Update update) {
        execute(
                sendMessage(e.toString(), update.getMessage().getChatId())
        );
    }

}
