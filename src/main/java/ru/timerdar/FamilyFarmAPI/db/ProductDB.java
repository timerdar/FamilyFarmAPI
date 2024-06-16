package ru.timerdar.FamilyFarmAPI.db;

import ru.timerdar.FamilyFarmAPI.dto.Product;

import java.sql.*;
import java.util.ArrayList;

public class ProductDB extends DatabaseController{

    public String addProduct(Product product){

        //System.out.println(product.toString());

        String query = "insert into product(name, price, eval) values(?, ?, ?);";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());
            preparedStatement.setBoolean(3, product.getEval());

            preparedStatement.executeUpdate();

            return "Добавлен продукт:\n" + product;
        }catch (SQLException e){
            return e.getMessage();
        }
    }

    public ArrayList<Product> productList(){
        ArrayList<Product> list = new ArrayList<>();
        String query = "select * from product order by name";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                Product product = new Product(rs.getString(2), rs.getDouble(3), rs.getBoolean(4));
                list.add(product);
            }
            return list;
        } catch (SQLException e){
            return null;
        }
    }

    public Product changePrice(Product product){
        String query = "update product set price = ? where name = ?";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setDouble(1, product.getPrice());
            preparedStatement.setString(2, product.getName());

            preparedStatement.executeUpdate();

            return product;
        }catch (SQLException e){
            return null;
        }
    }

    public String delete(String name){
        String query = "delete from product where name = ?";

        try(Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, name);

            int r = preparedStatement.executeUpdate();

            if (r == 0){
                return "Такого продукта нет, проверьте имя";
            }else{
                return name + " удален(а) из базы";
            }
        }catch (SQLException e){
            return null;
        }
    }

}
