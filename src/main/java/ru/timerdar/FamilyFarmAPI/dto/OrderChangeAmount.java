package ru.timerdar.FamilyFarmAPI.dto;


import lombok.Getter;

@Getter
public class OrderChangeAmount {

    private final Order order;
    private final double new_amount;


    public OrderChangeAmount(Order order, double newAmount) {
        this.order = order;
        this.new_amount = newAmount;
    }
}
