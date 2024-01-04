package ru.isb.bot.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@JsonDeserialize
@JsonSerialize
@JsonIgnoreProperties
class ChatGPTReceiptDTO {

    @JsonIgnore
    var choices: List<Choice>? = null

}