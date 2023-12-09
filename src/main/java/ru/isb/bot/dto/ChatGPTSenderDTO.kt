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
class ChatGPTSenderDTO (
    private var messages: MutableList<Message> = ArrayList()
) {

    val model = "gpt-3.5-turbo"

    fun setContent(content: String?) {
        val message = Message()
        message.role = "user"
        message.content = content
        messages.add(message)
    }

}