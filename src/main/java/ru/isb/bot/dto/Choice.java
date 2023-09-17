package ru.isb.bot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonSerialize
@JsonDeserialize
@JsonIgnoreProperties
public class Choice {

    private Long index;
    private Message message;
    
}