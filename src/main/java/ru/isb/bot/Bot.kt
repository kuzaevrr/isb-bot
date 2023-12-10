package ru.isb.bot

import kotlinx.coroutines.*
import lombok.SneakyThrows
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import ru.isb.bot.clients.NextcloudClient
import ru.isb.bot.enums.Commands
import ru.isb.bot.enums.Commands.Companion.fromString
import ru.isb.bot.services.MessageService
import ru.isb.bot.services.MessageServiceImpl
import ru.isb.bot.utils.MessageUtils.Companion.textSplitter
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

@OptIn(DelicateCoroutinesApi::class)
@Component
class Bot(
    private val messageService: MessageService,
    private val nextcloudClient: NextcloudClient
) : TelegramLongPollingBot(), Logging {

    @Value("\${bot.token}")
    private val botToken: String = ""

    @Value("\${bot.name}")
    private val botUsername: String = ""

    @Value("\${bot.isb.chat.id}")
    private val ISB_CHAT_ID: String = ""

    private var typingJob: Job? = null


    override fun getBotUsername(): String = botUsername
    override fun getBotToken(): String = botToken

    @SneakyThrows
    override fun onUpdateReceived(update: Update): Unit = runBlocking {
        GlobalScope.launch { asyncOnUpdateReceived(update) }
    }

    private fun asyncOnUpdateReceived(update: Update) {
        try {

            if (update.hasMessage()) {
                typingJob = GlobalScope.launch { typing(update.message.chatId) }
                switchMessage(update)
            }
        } catch (e: Exception) {
            logger.error(e.message ?: "Error onUpdateReceived", e)
            sendMessageException(e, update.message.chatId)
        } finally {
            typingJob?.cancel(null)
        }
    }

    @SneakyThrows
    private fun switchMessage(update: Update) {
        for (i in 1..1000000) {
            println(i*i)
        }
        if (update.message.hasText()) {
            handlerMessageText(update)
        } else if (update.message.hasDocument()) {
            handlerMessageDocument(update)
        }
    }

    @SneakyThrows
    private fun handlerMessageText(update: Update) {
        when (fromString(update.message.text)) {
            Commands.SCHEDULE_GROUP, Commands.SCHEDULE -> executeMessages(
                messageService.getSchedulesWeek(),
                update.message.chatId
            )

            Commands.LIST_GROUP, Commands.LIST -> executeMessages(
                messageService.getListGroup(),
                update.message.chatId
            )

            Commands.ALL, Commands.ALL_GROUP, Commands.ALL_LINK -> executeMessages(
                "@elektrik_gut @markin_ka @RA_prof @Mr_Ket1997 @Yureskii @V0xP0puli @vladka_teb @polibuu @Desert567",
                update.message.chatId
            )

            else -> {}
        }
        if (update.message.text.contains(MessageServiceImpl.MESSAGE_GPT_SPLIT)) {
            executeMessages(
                messageService.getAnswerGPTMessage(update.message.text),
                update.message.chatId
            )
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

    private fun typing(chatId: Long) {
        try {
            while (false == typingJob?.isCancelled) {
                execute(
                    SendChatAction(
                        chatId.toString(),
                        "TYPING",
                        Thread.currentThread().id.toInt()
                    )
                )
                Thread.sleep(3000)
            }
        } catch (e: Exception) {
            logger.error("Typing message error: ${e.message}", e)
        }
    }

    private fun executeMessages(message: String, chatId: Long) {
        textSplitter(message).forEach(Consumer<String> { message4096: String ->
            try {
                execute(
                    sendMessage(
                        message4096,
                        chatId
                    )
                )
            } catch (e: Exception) {
                logger.error(e.message ?: "Error executeMessages", e)
                sendMessageException(e, chatId)
            }
        })
    }

    private fun sendMessage(text: String, chatId: Long): SendMessage {
        val sendMessage = SendMessage()
        sendMessage.parseMode = ParseMode.HTML
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

}