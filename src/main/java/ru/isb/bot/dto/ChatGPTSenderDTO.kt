package ru.isb.bot.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import lombok.Getter
import lombok.Setter

@Setter
@Getter
@JsonSerialize
@JsonDeserialize
@JsonIgnoreProperties
class ChatGPTSenderDTO {

    var messages: MutableList<Message> = mutableListOf()

    val model = "gpt-3.5-turbo-16k-0613"

    fun setContent(content: String) : ChatGPTSenderDTO {
        val message = Message()
        message.role = "user"
        message.content = content
        messages.add(message)
        return this;
    }

}