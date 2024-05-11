package ru.timerdar.FamilyFarmAPI.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@JsonSerialize
@Getter
public class ProductOrdersList {
    private final String product_name;
    private final double sum;
    private final ArrayList<Order> orders;


    public ProductOrdersList(String productName, double sum, ArrayList<Order> orders) {
        product_name = productName;
        this.sum = sum;
        this.orders = orders;
    }
}
