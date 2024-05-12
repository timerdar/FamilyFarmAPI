package ru.timerdar.FamilyFarmAPI.controller;

import org.springframework.web.bind.annotation.*;
import ru.timerdar.FamilyFarmAPI.db.OrderDB;
import ru.timerdar.FamilyFarmAPI.dto.Consumer;
import ru.timerdar.FamilyFarmAPI.dto.ConsumerOrdersList;
import ru.timerdar.FamilyFarmAPI.dto.Order;
import ru.timerdar.FamilyFarmAPI.dto.ProductOrdersList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private OrderDB db = new OrderDB();

    @PostMapping("/add")
    public String addOrder(@RequestBody Order order){
        return db.addOrder(order);
    }

    @GetMapping("/undone")
    public ArrayList<ConsumerOrdersList> getUndoneOrders(){
        return db.getOrdersList("undone");
    }

    @GetMapping("/delivery")
    public ArrayList<ConsumerOrdersList> getDeliveryOrders(){
        return db.getOrdersList("delivery");
    }

    @GetMapping("/byProducts")
    public ArrayList<ProductOrdersList> getOrdersByProducts(){
        return db.getOrdersListByProducts();
    }

    @PostMapping("/toDelivery")
    public String toDelivery(@RequestBody Order order){
        return db.moveToDelivery(order);
    }


    @DeleteMapping("/delete")
    public String delete(@RequestBody Order order){
        return db.deleteOrder(order);
    }



    //TODO Сделать добавление комментариев к заказу(?) или к заказчику в доставке (?)
//    @PatchMapping("/comment")
//    public String addComment(@RequestBody Order order){
//        return db.addComment(order);
//    }
}
