package ru.timerdar.FamilyFarmAPI.dto;

import lombok.Getter;

@Getter
public class Consumer {
    private final String name;
    private final String street;
    private final String room;
    private final String district;
    private final String phone;

    public Consumer(String name, String street, String room, String district, String phone) {
        this.name = name;
        this.street = street;
        this.room = room;
        this.district = district;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return name + " " + street + " " + room + " " + district + " " + phone;
    }
}
