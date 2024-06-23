package ru.timerdar.FamilyFarmAPI.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Marker {
    private final int marker;

    public Marker(int marker){
        this.marker = marker;
    }

    public Marker(){
        this.marker = 0;
    }
}
