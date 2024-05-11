package ru.timerdar.FamilyFarmAPI.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseController {

    private static final String db_url = System.getenv("DBURL") + System.getenv("DBNAME");
    private static final String db_user = System.getenv("DBUSER");
    private static final String db_password = System.getenv("DBPASSWORD");

    public Connection getConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(db_url, db_user, db_password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}