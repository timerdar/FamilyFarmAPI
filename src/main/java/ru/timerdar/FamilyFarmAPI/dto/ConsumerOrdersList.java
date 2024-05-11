package ru.timerdar.FamilyFarmAPI.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@JsonSerialize
public class ConsumerOrdersList {
    private final Consumer consumer;
    private final ArrayList<Order> orders;

    public ConsumerOrdersList(Consumer consumer, ArrayList<Order> orders) {
        this.consumer = consumer;
        this.orders = orders;
    }
}
