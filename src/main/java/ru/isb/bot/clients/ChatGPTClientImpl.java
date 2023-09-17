package ru.isb.bot.clients;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.isb.bot.dto.ChatGPTReceiptDTO;
import ru.isb.bot.dto.ChatGPTSenderDTO;
import ru.isb.bot.utils.JsonUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Log4j2
@Component
public class ChatGPTClientImpl implements ChatGPTClient {

    @Value("${gpt.key}")
    private String apiKey;

    @Override
    @SneakyThrows
    public String getAnswerGPT(String message) {

        HttpClient httpClient = HttpClient.newHttpClient();

        ChatGPTSenderDTO chatGPTSenderDTO = new ChatGPTSenderDTO();
        chatGPTSenderDTO.setContent(message);

        // Создать HttpRequest с методом POST и установить заголовки
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://api.openai.com/v1/chat/completions"))
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtils.parseObjectToString(chatGPTSenderDTO)))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .build();

        // Отправить запрос и получить ответ
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            try {
                ChatGPTReceiptDTO dto = JsonUtils.parseStringJsonToObject(response.body(), ChatGPTReceiptDTO.class);
                if (dto != null && !dto.getChoices().isEmpty() && dto.getChoices().get(0) != null && dto.getChoices().get(0).getMessage() != null) {
                    return dto.getChoices().get(0).getMessage().getContent();
                } else {
                    return "ChatGPT отправил не известный объект.\n" + response.body();
                }
            } catch (Exception e) {
                throw new RuntimeException("Ошибка парсинга body от ChatGPT: " + e.getMessage());
            }
        } else {
            return String.format("Код ошибки: %s\nТекст ошибки: %s", response.statusCode(), response.body());
        }
    }

}
