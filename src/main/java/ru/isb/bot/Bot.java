package ru.isb.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.isb.chat.id}")
    private String ISB_CHAT_ID;

    private final MessageServiceImpl messageService;


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().getText() != null) {
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
                        case ALL -> execute(
                                sendMessage(
                                        "@elektrik_gut @markin_ka @RA_prof @Mr_Ket1997 @Yureskii @Va1er1ev1ch @vladka_teb @Полина @Desert567",
                                        update.getMessage().getChatId()
                                )
                        );
                        default -> {
                        }
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
