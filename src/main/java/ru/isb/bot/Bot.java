package ru.isb.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.isb.bot.enums.Commands;
import ru.isb.bot.services.MessageServiceImpl;

@Log4j2
@Component
@Getter
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.test.chat.id}")
    private String ISB_CHAT_ID;


    @Autowired
    MessageServiceImpl messageService;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().getText() != null) {
                    if (Commands.SCHEDULE_GROUP.getCommand().equals(update.getMessage().getText()) ||
                        Commands.SCHEDULE.getCommand().equals(update.getMessage().getText())) {
                        execute(
                                sendMessage(
                                        messageService.getSchedulesWeek(),
                                        update.getMessage().getChatId()
                                ));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            execute(
                    sendMessage(
                            e.toString(),
                            update.getMessage().getChatId()
                    ));
            e.printStackTrace();
        }
    }

    private SendMessage sendMessage(String text, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setParseMode(ParseMode.HTML);
        sendMessage.setText(text);
        sendMessage.setChatId(chatId.toString());

        return sendMessage;
    }
}
