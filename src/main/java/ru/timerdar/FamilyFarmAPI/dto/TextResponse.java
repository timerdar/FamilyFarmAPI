package ru.timerdar.FamilyFarmAPI.dto;

import lombok.Getter;

@Getter
public class TextResponse {

    private final String message;

    public TextResponse(String message){
        this.message = message;
    }
}
