package ru.timerdar.FamilyFarmAPI.db;

import ru.timerdar.FamilyFarmAPI.dto.Consumer;

import java.sql.*;
import java.util.ArrayList;

public class ConsumerDB extends DatabaseController {


    /**
     * Метод для добавления заказчика в бд. Проверяет, есть ли такой район в бд, если есть, подставляет district_id. Если нет, добавляет сначала район, потом подставляет его dustrict_id.
     *
     * @param consumer заказчик, которого нужно добавить (Имя, Улица, ДОМ/КВ, Район, Телефон)
     * @return возвращает строку о добавлении данного пользователя или описание ошибки
     */
    public String addConsumer(Consumer consumer){
        String count = "select count(id) from district where district = '" + consumer.getDistrict() + "'"; // для проверки, есть ли такой район в бд
        String subQuery = "select id from district where district = '" + consumer.getDistrict() + "'"; //для получения district_id по названию района
        String query = "insert into consumer(name, street, room, district_id, phone)" +
                "values(?, ?, ?, ?, ?)"; //добавление заказчкика

        try(Connection connection = getConnection();
            Statement countStatement = connection.createStatement();
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            //проверка наличия района и добавление в ином случае
            ResultSet countRs = countStatement.executeQuery(count);
            if (countRs.next()){
                if (countRs.getInt(1) == 0){
                    PreparedStatement addDistrict = connection.prepareStatement("insert into district(district) values(?)");
                    addDistrict.setString(1, consumer.getDistrict());
                    addDistrict.executeUpdate();
                }
            }

            //получение district_id по названию для след. запроса
            ResultSet resultSet = statement.executeQuery(subQuery);
            int district_id = 0;
            if (resultSet.next()){
                district_id = resultSet.getInt(1);
            }

            preparedStatement.setString(1, consumer.getName());
            preparedStatement.setString(2, consumer.getStreet());
            preparedStatement.setString(3, consumer.getRoom());
            preparedStatement.setInt(4, district_id);
            preparedStatement.setString(5, consumer.getPhone());

            preparedStatement.executeUpdate();

            return "Добавлен заказчик:\n" + consumer;
        }catch (SQLException e){
            return e.getMessage();
        }
    }

    /**
     * Метод выдает список заказчиков определенного района
     * @param district район, по которому ищем заказчиков
     * @return возварщает список заказчиков
     */
    public ArrayList<Consumer> consumerListByDistrict(String district){
        ArrayList<Consumer> consumers = new ArrayList<>();
        String districtQuery = "select id from district where district = '" + district + "'";
        String query = "select name, street, room, phone " +
                "from consumer " +
                "where district_id = ?";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(districtQuery);
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            int district_id = 0;
            if (resultSet.next()){
                district_id = resultSet.getInt("id");
            }

            preparedStatement.setInt(1, district_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Consumer consumer = new Consumer(rs.getString(1), rs.getString(2), rs.getString(3), district, rs.getString(4));
                consumers.add(consumer);
            }
            return consumers;
        }catch (SQLException e){
            return null;
        }
    }

    public ArrayList<Consumer> getAllConsumers(){
        ArrayList<Consumer> list = new ArrayList<>();
        String query = "select * from consumer";
        String subQuery = "select district from district where id = ";

        try(Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query)){

            while (resultSet.next()){
                String district_name = "";
                Statement subStatement = connection.createStatement();
                ResultSet district = subStatement.executeQuery(subQuery + resultSet.getString(5));
                if (district.next()){
                    district_name = district.getString(1);
                }
                Consumer consumer = new Consumer(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), district_name, resultSet.getString(6));
                list.add(consumer);
            }
            return list;
        }catch (SQLException e){
            return null;
        }
    }

    public ArrayList<String> getDistrictsList(){
        ArrayList<String> districtList = new ArrayList<>();
        String query = "select district from district";

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query)){

            while (resultSet.next()){
                districtList.add(resultSet.getString(1));
            }
            return districtList;
        }catch (SQLException e){
            return null;
        }
    }

    public String delete(String name){
        String query = "delete from consumer where name = ?";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, name);

            int r = preparedStatement.executeUpdate();

            if (r == 0){
                return "Такого заказчика нет, проверьте имя";
            }else{
                return name + " удален(а) из базы";
            }
        }catch (SQLException e){
            return null;
        }
    }

    public Consumer getConsumerByName(String name){
        String query = "select * from consumer where name = " + name;

        try(Connection connection = getConnection()){
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            rs.next();

            return new Consumer(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
        }catch(SQLException e){
            return null;
        }
    }
}
