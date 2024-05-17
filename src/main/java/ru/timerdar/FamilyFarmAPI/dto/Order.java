package ru.timerdar.FamilyFarmAPI.dto;

import lombok.Getter;

import java.util.Date;

@Getter
public class Order {
    private final String consumer_name;
    private final String  product_name;
    private final java.sql.Date start_data;
    private final double amount;


    public Order(String consumer_name, String product_name, double amount) {
        this.consumer_name = consumer_name;
        this.product_name = product_name;
        Date d = new Date();
        this.start_data = new java.sql.Date(d.getTime());
        this.amount = amount;
    }

    @Override
    public String toString() {
        return consumer_name + " " + product_name + " " + amount;
    }
}
