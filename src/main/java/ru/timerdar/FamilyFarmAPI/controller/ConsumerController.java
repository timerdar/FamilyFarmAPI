package ru.timerdar.FamilyFarmAPI.controller;

import org.springframework.web.bind.annotation.*;
import ru.timerdar.FamilyFarmAPI.db.ConsumerDB;
import ru.timerdar.FamilyFarmAPI.dto.Consumer;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    private final ConsumerDB db = new ConsumerDB();

    @PostMapping("/add")
    public String addConsumer(@RequestBody Consumer consumer){
        return db.addConsumer(consumer);
    }

    @GetMapping("/byDistrict/{district}")
    public ArrayList<Consumer> getConsumerListByDistrict(@PathVariable String district){
        return db.consumerListByDistrict(district);
    }

    @GetMapping("/districts")
    public ArrayList<String> getDistrictsList(){
        return db.getDistrictsList();
    }

    @DeleteMapping("/delete")
    public String deleteConsumer(@RequestBody String name){
        return db.delete(name);
    }

    @GetMapping("/")
    public ArrayList<Consumer> getAllConsumers(){
        return db.getAllConsumers();
    }
}
