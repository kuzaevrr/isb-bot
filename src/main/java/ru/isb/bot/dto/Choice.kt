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
class Choice {

    var index: Long = 0
    var message: Message? = null
}