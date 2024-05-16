package ru.timerdar.FamilyFarmAPI.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.timerdar.FamilyFarmAPI.db.DatabaseController;
import ru.timerdar.FamilyFarmAPI.dto.TextResponse;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/common")
public class CommonController {

    @GetMapping("/hello")
    public TextResponse hello(){
        return new TextResponse("АПИ не спит, работает.");
    }

    @GetMapping("/db")
    public TextResponse dbStatus(){
        DatabaseController db = new DatabaseController();
        String message;
        try(Connection c = db.getConnection()){
            message = "Соединение с БД активно";
        }catch (SQLException e){
            message = e.getMessage();
        }
        return new TextResponse(message);
    }

}
