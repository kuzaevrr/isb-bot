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
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.isb.bot.clients.NextcloudClient;
import ru.isb.bot.enums.Commands;
import ru.isb.bot.services.MessageServiceImpl;
import ru.isb.bot.utils.MessageUtils;

import static ru.isb.bot.services.MessageServiceImpl.MESSAGE_GPT_SPLIT;

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

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                switchMessage(update);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessageException(e, update.getMessage().getChatId());
        }
    }

    @SneakyThrows
    private void switchMessage(Update update) {
        if (update.getMessage().hasText()) {
            handlerMessageText(update);
        } else if (update.getMessage().hasDocument()) {
            handlerMessageDocument(update);
        }
    }

    @SneakyThrows
    private void handlerMessageText(Update update) {
        switch (Commands.fromString(update.getMessage().getText())) {
            case SCHEDULE_GROUP, SCHEDULE -> executeMessages(
                    messageService.getSchedulesWeek(),
                    update.getMessage().getChatId()
            );
            case LIST_GROUP, LIST -> executeMessages(
                    messageService.getListGroup(),
                    update.getMessage().getChatId()
            );
            case ALL, ALL_GROUP, ALL_LINK -> executeMessages(
                    "@elektrik_gut @markin_ka @RA_prof @Mr_Ket1997 @Yureskii @Va1er1ev1ch @vladka_teb @polibuu @Desert567",
                    update.getMessage().getChatId()
            );
        }
        if (update.getMessage().getText().contains(MESSAGE_GPT_SPLIT)) {
            executeMessages(
                    getAnswerGPTMessageAndTyping(update),
                    update.getMessage().getChatId()
            );
        }
    }

    private String getAnswerGPTMessageAndTyping(Update update) {
        Thread typingThread = getTypingThread(update.getMessage().getChatId());
        typingThread.start();

        String answer = messageService.getAnswerGPTMessage(update.getMessage().getText());

        typingThread.stop();

        return answer;
    }
    @SneakyThrows
    private void handlerMessageDocument(Update update) {
        Document document = update.getMessage().getDocument();
        messageService.sendFileToNextcloud(
                downloadFile(
                        execute(new GetFile(document.getFileId())).getFilePath()
                ),
                document.getFileName()
        );
    }

    private Thread getTypingThread(Long chatId) {
        return new Thread(() -> {
            try {
                while (true) {
                    execute(new SendChatAction(String.valueOf(chatId), "TYPING", (int) Thread.currentThread().getId()));
                    Thread.sleep(2800);
                }
            } catch (Exception e) {
                log.error("Typing message error: {}", e.getMessage(), e);
            }
        });
    }

    private void executeMessages(String message, Long chatId) {
        MessageUtils.textSplitter(message).forEach(message4096 -> {
            try {
                execute(
                        sendMessage(
                                message4096,
                                chatId
                        )
                );
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                sendMessageException(e, chatId);
            }
        });
    }

    private SendMessage sendMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    @SneakyThrows
    private void sendMessageException(Exception e, Long chatId) {
        execute(
                sendMessage(e.toString(), chatId)
        );
    }

}
