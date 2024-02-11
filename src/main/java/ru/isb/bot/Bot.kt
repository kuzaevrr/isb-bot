package ru.isb.bot

import kotlinx.coroutines.*
import lombok.SneakyThrows
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.isb.bot.enums.Commands
import ru.isb.bot.enums.Commands.Companion.fromString
import ru.isb.bot.services.MessageService
import ru.isb.bot.utils.MessageUtils
import ru.isb.bot.utils.MessageUtils.Companion.textSplitter
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.function.Consumer


@OptIn(DelicateCoroutinesApi::class)
@Component
class Bot(
    private val messageService: MessageService
) : TelegramLongPollingBot(), Logging {

    @Value("\${bot.token}")
    private val botToken: String = ""

    @Value("\${bot.name}")
    private val botUsername: String = ""

    @Value("\${bot.isb.chat.id}")
    private val ISB_CHAT_ID: String = ""

    private var gptEnableMap = HashMap<Long, Boolean>();

    override fun getBotUsername(): String = botUsername
    override fun getBotToken(): String = botToken

    private var job: Job? = null;

    @SneakyThrows
    override fun onUpdateReceived(update: Update) {
        val executor = Executors.newCachedThreadPool()
        val future = CompletableFuture<Void>();
        executor.execute() {
            future.thenRunAsync {
                asyncOnUpdateReceived(update)
            }
        }

        future.complete(null)
        executor.shutdown()
    }


    private fun asyncOnUpdateReceived(update: Update) {
        try {
            if (update.hasMessage()) {
                switchMessage(update)
            }
        } catch (e: Exception) {
            logger.error("Error onUpdateReceived ${e.message}", e)
            sendMessageException(e, update.message.chatId)
        } finally {
            job?.cancel()
        }
    }

    @SneakyThrows
    private fun switchMessage(update: Update) {
        if (update.message.hasText()) {
            handlerMessageText(update)
        } else if (update.message.hasDocument()) {
            handlerMessageDocument(update)
        }
    }

    @SneakyThrows
    private fun handlerMessageText(update: Update) {
        when (fromString(update.message.text)) {

            Commands.SCHEDULE_GROUP, Commands.SCHEDULE -> {
                job = GlobalScope.launch { typing(update.message.chatId) }
                executeMessages(
                    messageService.getSchedulesWeek(),
                    update
                )
            }

            Commands.LIST_GROUP, Commands.LIST -> {
                job = GlobalScope.launch { typing(update.message.chatId) }
                executeMessages(
                    messageService.getListGroup(),
                    update
                )
            }

            Commands.ALL, Commands.ALL_GROUP, Commands.ALL_LINK -> {
                job = GlobalScope.launch { typing(update.message.chatId) }
                executeMessages(
                "@kuzya\\_ram @markin\\_ka @RA\\_prof @Mr\\_Ket1997 @Yureskii @V0xP0puli @vladka\\_teb @polibuu @Desert567",
                    update
                )
            }

            Commands.HELP -> {
                job = GlobalScope.launch { typing(update.message.chatId) }
                executeMessages(
                    """
                        /schedule - Расписание занятий
                        /list - Список группы
                        /all - Вызвать всех участников группы
                        /help - Пояснение команд
                        /gpt - Команда включения режима GPT
                    """.trimIndent(),
                    update
                )
            }

            Commands.GPT -> {
                executeMessages(
                    """
                        ${if (switchingGptMode(update)) "Включен GPT режим. Бот будет" else "Отключен GPT режим. Бот не будет"} отвечать на все сообщения.
                    """.trimIndent(),
                    update
                )
            }

            else -> {
              if (gptEnableMap[update.message.chatId] == true) {
                    job = GlobalScope.launch { typing(update.message.chatId) }
                    executeMessages(
                        messageService.getAnswerGPTMessage(MessageUtils.concatInputMessage(update.message.text)),
                        update
                    )
              } else {}
            }
        }
    }

    @SneakyThrows
    private fun handlerMessageDocument(update: Update) {
        val document = update.message.document
        messageService.sendFileToNextcloud(
            downloadFile(
                execute(GetFile(document.fileId)).filePath
            ),
            document.fileName
        )
    }

    private suspend fun typing(chatId: Long) {
        try {
            while (job?.isActive == true) {
                execute(
                    SendChatAction(
                        chatId.toString(),
                        "TYPING",
                        Thread.currentThread().id.toInt()
                    )
                )
                delay(3000)
            }
        } catch (e: Exception) {
            logger.error("Typing message error: ${e.message}", e)
        }
    }

    private fun executeMessages(message: String, update: Update) {
        textSplitter(message).forEach(Consumer { message4096: String ->
            try {
                execute(
                    sendMessage(
                        message4096,
                        update.message.chatId
                    )
                )
            } catch (e: Exception) {
                logger.info("Info executeMessages: $message4096")
                logger.error(e.message ?: "Error executeMessages", e)
                sendMessageException(e, update.message.chatId)
            }
        })
    }

    private fun sendMessage(text: String, chatId: Long): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.parseMode = ParseMode.MARKDOWN
        sendMessage.text = text
        sendMessage.chatId = chatId.toString()
        return sendMessage
    }

    @SneakyThrows
    private fun sendMessageException(e: Exception, chatId: Long) {
        execute(
            sendMessage(e.toString(), chatId)
        )
    }

    private fun switchingGptMode(update: Update) : Boolean {
        val chatId = update.message.chatId;
        val enableGpt = gptEnableMap[chatId]?:false;

        gptEnableMap.put(chatId, !enableGpt)
        return !enableGpt
    }

}