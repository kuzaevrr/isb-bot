package ru.isb.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonDeserialize
@JsonSerialize
@JsonIgnoreProperties
public class ChatGPTReceiptDTO {

    private List<Choice> choices;

}



