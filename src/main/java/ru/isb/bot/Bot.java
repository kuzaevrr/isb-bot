package ru.isb.bot;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.isb.bot.services.MessageServiceImpl;

@Log4j2
@Component
@Getter
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @Autowired
    MessageServiceImpl messageService;

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().getText() != null) {
                    if (update.getMessage().getText().equals("не пишка")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setParseMode("Markdown");
                        sendMessage.setText("Привет, я *новый* _бот_ группы *ИСб*, не ПИшек");
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    }
                    if (update.getMessage().getText().equals("Получить расписание")) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setParseMode("Markdown");
                        sendMessage.setText(messageService.getJsonClient());
                        sendMessage.setChatId(update.getMessage().getChatId().toString());
                        execute(sendMessage);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(e.toString());
            sendMessage.setChatId(update.getMessage().getChatId().toString());

            execute(sendMessage);
            e.printStackTrace();
        }
    }
}
