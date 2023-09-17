package ru.isb.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@JsonSerialize
@JsonDeserialize
@JsonIgnoreProperties
public class ChatGPTSenderDTO {

    private String model = "gpt-3.5-turbo";

    private List<Message> messages = new ArrayList<>();

    public void setContent(String content) {
        Message message = new Message();
        message.setRole("user");
        message.setContent(content);
        messages.add(message);
    }

}


