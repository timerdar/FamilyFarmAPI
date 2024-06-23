package ru.timerdar.FamilyFarmAPI.controller;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.timerdar.FamilyFarmAPI.db.ConsumerDB;
import ru.timerdar.FamilyFarmAPI.dto.Consumer;
import ru.timerdar.FamilyFarmAPI.dto.Marker;
import ru.timerdar.FamilyFarmAPI.dto.TextResponse;

import java.util.ArrayList;

@RestController
@RequestMapping("/api/consumers")
public class ConsumerController {

    private final ConsumerDB db = new ConsumerDB();

    @PostMapping("/add")
    public TextResponse addConsumer(@RequestBody Consumer consumer){
        return new TextResponse(db.addConsumer(consumer));
    }

    @GetMapping("/byDistrict/{district}")
    public ArrayList<Consumer> getConsumerListByDistrict(@PathVariable String district){
        return db.consumerListByDistrict(district);
    }

    @GetMapping("/districts")
    public ArrayList<String> getDistrictsList(){
        return db.getDistrictsList();
    }

    @DeleteMapping("/delete/{name}")
    public TextResponse deleteConsumer(@PathVariable String name){
        return new TextResponse(db.delete(name));
    }

    @PatchMapping("/{name}/mark")
    public void setMarker(@PathVariable String name, @RequestBody Marker marker){
        db.setMarker(name, marker.getMarker());
    }

    @GetMapping("/")
    public ArrayList<Consumer> getAllConsumers(){
        return db.getAllConsumers();
    }

    @GetMapping("/{name}")
    public Consumer getConsumerByName(@PathVariable String name){
        return db.getConsumerByName(name);
    }
}
