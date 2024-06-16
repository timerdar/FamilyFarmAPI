package ru.timerdar.FamilyFarmAPI.db;

import org.jetbrains.annotations.NotNull;
import ru.timerdar.FamilyFarmAPI.dto.*;

import java.sql.*;
import java.util.ArrayList;
public class OrderDB extends DatabaseController {

    public String addOrder(@NotNull Order order) {

        String existsConsumer = "select count(name) from consumer where name = '" + order.getConsumer_name() + "';";
        String existsProduct = "select count(name) from product where name = '" + order.getProduct_name() + "';";
        String getConsumerId = "select id from consumer where name = '" + order.getConsumer_name() + "';";
        String getProductId = "select id from product where name = '" + order.getProduct_name() + "';";
        String query = "insert into \"order\"(product_id, consumer_id, start_data, amount) values(?, ?, ?, ?);";

        try (Connection connection = getConnection();
             Statement existCons = connection.createStatement();
             Statement existProd = connection.createStatement();
             Statement getCons = connection.createStatement();
             Statement getProd = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ) {

            ResultSet consumers = existCons.executeQuery(existsConsumer);
            if (consumers.next()) {
                if (consumers.getInt(1) == 0) {
                    throw new Exception("Нет заказчика в базе. Сначала необходимо добавить заказчика.\n/add_c");
                }
            }

            ResultSet products = existProd.executeQuery(existsProduct);
            if (products.next()) {
                if (products.getInt(1) == 0) {
                    throw new Exception("Нет продукта в базе. Сначала необходимо добавить продукт.\n/add_p");
                }
            }

            ResultSet consumer = getCons.executeQuery(getConsumerId);
            consumer.next();
            int consumer_id = consumer.getInt(1);

            ResultSet product = getProd.executeQuery(getProductId);
            product.next();
            int product_id = product.getInt(1);

            preparedStatement.setInt(1, product_id);
            preparedStatement.setInt(2, consumer_id);
            preparedStatement.setDate(3, order.getStart_data());
            preparedStatement.setDouble(4, order.getAmount());

            preparedStatement.executeUpdate();

            return "Добавлен заказ: " + order;
        } catch (Exception e) {
            return "Проверьте наличие данного продукта и имя заказчика";
        }
    }

    public ArrayList<ConsumerOrdersList> getOrdersList(@NotNull String view) {
        String table, label;
        ArrayList<ConsumerOrdersList> answer = new ArrayList<>();
        label = switch (view) {
            default -> {
                table = "ERROR";
                yield "ERROR";
            }
            case "undone" -> {
                table = "undone_orders";
                yield "Незакрытые заказы:\n\n";
            }
            case "delivery" -> {
                table = "delivery";
                yield "В доставке:\n\n";
            }
        };
        String query = "select * from " + table + " where consumer_id = ?;";
        String allConsumers = "select distinct consumer_id from " + table + ";";
        String getCons = "select * from consumer where id = ?;";
        String getProd = "select name from product where id = ?;";
        String getDistrict = "select district from district where id = ?";

        //Получаем список consumer_id у кого есть заказ
        try (Connection connection = getConnection();
             Statement consumers = connection.createStatement()) {
            ResultSet resultSet = consumers.executeQuery(allConsumers);
            while (resultSet.next()) {

                ArrayList<Order> list = new ArrayList<>();

                int consumer_id = resultSet.getInt(1);

                PreparedStatement consumName = connection.prepareStatement(getCons);
                consumName.setInt(1, consumer_id);
                ResultSet consum = consumName.executeQuery();
                consum.next();//получили одного заказчика

                PreparedStatement districtName = connection.prepareStatement(getDistrict);
                districtName.setInt(1, consum.getInt(5));
                ResultSet district = districtName.executeQuery();
                district.next();

                Consumer consumer = new Consumer(consum.getString(2), consum.getString(3), consum.getString(4), district.getString(1), consum.getString(6));

                PreparedStatement orderSt = connection.prepareStatement(query);
                orderSt.setInt(1, consumer_id);
                ResultSet ordersRS = orderSt.executeQuery();
                while (ordersRS.next()) {
                    //Получить название каждого продукта из заказов
                    PreparedStatement getProdSt = connection.prepareStatement(getProd);
                    getProdSt.setInt(1, ordersRS.getInt("product_id"));
                    ResultSet productRS = getProdSt.executeQuery();
                    productRS.next();

                    Order order = new Order(consum.getString(2), productRS.getString(1), ordersRS.getDouble("amount"));

                    list.add(order);
                }
                answer.add(new ConsumerOrdersList(consumer, list));
            }
            return answer;
        } catch (Exception e) {
            return null;
        }
    }


    public ArrayList<ProductOrdersList> getOrdersListByProducts() {
        ArrayList<ProductOrdersList> list = new ArrayList<>();

        String products = "select distinct product_id from undone_orders";
        String sum = "select sum(amount) from undone_orders where product_id = ?";
        String ordersList = "select id, consumer_id, amount from undone_orders where product_id = ?";
        String consumerName = "select name from consumer where id = ?";
        String productName = "select name from product where id = ?";

        try (Connection connection = getConnection();
             Statement products_statement = connection.createStatement()) {

            ResultSet products_ids = products_statement.executeQuery(products);

            while (products_ids.next()) {
                int product_id = products_ids.getInt(1);

                ArrayList<Order> orders = new ArrayList<>();

                //получение имени продукта
                PreparedStatement product_name_statement = connection.prepareStatement(productName);
                product_name_statement.setInt(1, product_id);
                ResultSet product_name_rs = product_name_statement.executeQuery();
                product_name_rs.next();
                String product_name = product_name_rs.getString(1);

                //получение суммы заказанных позиций
                PreparedStatement sum_statement = connection.prepareStatement(sum);
                sum_statement.setInt(1, product_id);
                ResultSet sum_rs = sum_statement.executeQuery();
                sum_rs.next();
                double sum_number = sum_rs.getDouble(1);

                //получение списка заказов по каждой позиции
                PreparedStatement order_list_statement = connection.prepareStatement(ordersList);
                order_list_statement.setInt(1, product_id);
                ResultSet orders_rs = order_list_statement.executeQuery();
                while (orders_rs.next()) {
                    int order_id = orders_rs.getInt(1);
                    int consumer_id = orders_rs.getInt(2);
                    double amount = orders_rs.getDouble(3);

                    //получение имени заказчика и кол-ва данной позиции
                    PreparedStatement consumer_name_statement = connection.prepareStatement(consumerName);
                    consumer_name_statement.setInt(1, consumer_id);
                    ResultSet consumer_name_rs = consumer_name_statement.executeQuery();
                    consumer_name_rs.next();
                    String consumer_name = consumer_name_rs.getString(1);

                    Order order = new Order(consumer_name, product_name, amount);
                    //System.out.println(order);
                    orders.add(order);
                }
                ProductOrdersList productOrdersList = new ProductOrdersList(product_name, sum_number, orders);
                //System.out.println(productOrdersList);
                list.add(productOrdersList);
            }
            return list;

        } catch (Exception e) {
            return null;
        }
    }


    //переводит в доставку заказ. Если такой заказ повторяется - переводит тот, что раньше был добавлен в бд
    public String moveToDelivery(Order order) {
        String query = "update \"order\" set status_id = 2 where id = ?";
        String consumer_name = "select id from consumer where name = ?";
        String product_name = "select id from product where name = ?";
        String order_id = "select id from undone_orders where product_id = ? and consumer_id = ? and amount = ? order by start_data asc limit 1";
        try (Connection connection = getConnection()) {

            PreparedStatement consumer_ps = connection.prepareStatement(consumer_name);
            PreparedStatement product_ps = connection.prepareStatement(product_name);
            PreparedStatement order_ps = connection.prepareStatement(order_id);

            consumer_ps.setString(1, order.getConsumer_name());
            product_ps.setString(1, order.getProduct_name());

            ResultSet consumer_id = consumer_ps.executeQuery();
            ResultSet product_id = product_ps.executeQuery();
            consumer_id.next();
            product_id.next();

            int first = product_id.getInt(1);
            int second = consumer_id.getInt(1);

            order_ps.setInt(1, first);
            order_ps.setInt(2, second);
            order_ps.setDouble(3, order.getAmount());

            ResultSet order_id_rs = order_ps.executeQuery();
            order_id_rs.next();

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order_id_rs.getInt(1));
            preparedStatement.executeUpdate();
            return "Заказ переведен в доставку: " + order;
        } catch (Exception e) {
            return null;
        }
    }

    //переводит из доставки заказ. Если такой заказ повторяется - переводит тот, что раньше был добавлен в бд
    public String moveToUndone(Order order) {
        String query = "update \"order\" set status_id = 1 where id = ?";
        String consumer_name = "select id from consumer where name = ?";
        String product_name = "select id from product where name = ?";
        String order_id = "select id from delivery where product_id = ? and consumer_id = ? and amount = ? order by start_data asc limit 1";
        try (Connection connection = getConnection()) {

            PreparedStatement consumer_ps = connection.prepareStatement(consumer_name);
            PreparedStatement product_ps = connection.prepareStatement(product_name);
            PreparedStatement order_ps = connection.prepareStatement(order_id);

            consumer_ps.setString(1, order.getConsumer_name());
            product_ps.setString(1, order.getProduct_name());

            ResultSet consumer_id = consumer_ps.executeQuery();
            ResultSet product_id = product_ps.executeQuery();
            consumer_id.next();
            product_id.next();

            int first = product_id.getInt(1);
            int second = consumer_id.getInt(1);

            order_ps.setInt(1, first);
            order_ps.setInt(2, second);
            order_ps.setDouble(3, order.getAmount());

            ResultSet order_id_rs = order_ps.executeQuery();
            order_id_rs.next();

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, order_id_rs.getInt(1));
            preparedStatement.executeUpdate();
            return "Заказ выведен из доставки: " + order;
        } catch (Exception e) {
            return null;
        }
    }

    //удаляет заказ. если таких заказов несколько - удаляет самый старый
    public String deleteOrder(Order order) {
        String query = "delete from \"order\" where id = (select id from \"order\" where product_id = ? and consumer_id = ? and start_data = ? and status_id < 3 order by start_data asc limit 1)";
        String consumer_name = "select id from consumer where name = ?";
        String product_name = "select id from product where name = ?";

        try (Connection connection = getConnection()) {

            PreparedStatement consumer_name_ps = connection.prepareStatement(consumer_name);
            PreparedStatement product_name_ps = connection.prepareStatement(product_name);
            PreparedStatement delete_order_ps = connection.prepareStatement(query);

            consumer_name_ps.setString(1, order.getConsumer_name());
            product_name_ps.setString(1, order.getProduct_name());

            ResultSet consumer_id_rs = consumer_name_ps.executeQuery();
            ResultSet product_id_rs = product_name_ps.executeQuery();

            consumer_id_rs.next();
            product_id_rs.next();

            delete_order_ps.setInt(1, product_id_rs.getInt(1));
            delete_order_ps.setInt(2, consumer_id_rs.getInt(1));
            delete_order_ps.setDate(3, order.getStart_data());

            int r = delete_order_ps.executeUpdate();

            if (r > 0) {
                return "Заказ удален:\n" + order;
            } else {
                return "Такого заказа нет";
            }
        } catch (SQLException e) {
            return null;
        }
    }

    public Order changeOrderAmount(OrderChangeAmount order){
        String query = "update \"order\" set amount = ? where id = (select id from \"order\" where consumer_id = ? and product_id = ? and amount = ? order by start_data asc limit 1)";
        String consumer_name = "select id from consumer where name = ?";
        String product_name = "select id from product where name = ?";

        try (Connection connection = getConnection()) {

            PreparedStatement consumer_name_ps = connection.prepareStatement(consumer_name);
            PreparedStatement product_name_ps = connection.prepareStatement(product_name);
            PreparedStatement change_order_ps = connection.prepareStatement(query);

            consumer_name_ps.setString(1, order.getOrder().getConsumer_name());
            product_name_ps.setString(1, order.getOrder().getProduct_name());

            ResultSet consumer_id_rs = consumer_name_ps.executeQuery();
            ResultSet product_id_rs = product_name_ps.executeQuery();

            consumer_id_rs.next();
            product_id_rs.next();

            change_order_ps.setInt(3, product_id_rs.getInt(1));
            change_order_ps.setInt(2, consumer_id_rs.getInt(1));
            change_order_ps.setDouble(4, order.getOrder().getAmount());
            change_order_ps.setDouble(1, order.getNew_amount());

            change_order_ps.executeUpdate();

            return new Order(order.getOrder().getConsumer_name(), order.getOrder().getProduct_name(), order.getNew_amount());
        }catch (SQLException e){
            return null;
        }
    }

    public TextResponse clearDelivery(){
        String query = "call clear_delivery();";

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement()){
            statement.executeQuery(query);
            return new TextResponse("Clear");
        }catch (Exception e) {
            System.out.println(e.getClass() + " " + e.getMessage() + " в процессе чистки доставки");
            return new TextResponse("Доставка очищена");
        }
    }


    //TODO Добавить реализацию запроса addComment
//    public String addComment(Order order){
//        String query = "update \"order\" set comment = ? where id = (select id from \"order\" where product_id = ? and consumer_id = ? and amount = ?)";
//        String consumer_id = "select id from consumer where name = ?";
//        String product_id = "select id from product where name = ?";
//
//        try(Connection connection = getConnection();
//            PreparedStatement statement = connection.prepareStatement(query)) {
//
//            PreparedStatement consumer_id_ps = connection.prepareStatement(consumer_id);
//            PreparedStatement product_id_ps = connection.prepareStatement(product_id);
//
//            consumer_id_ps.setString(1, order.getConsumer_name());
//            product_id_ps.setString(1, order.getProduct_name());
//
//            ResultSet consumer_id_rs = consumer_id_ps.executeQuery();
//            ResultSet product_id_rs = product_id_ps.executeQuery();
//
//            consumer_id_rs.next();
//            product_id_rs.next();
//
//            statement.setString(1, order.getComment());
//            statement.setInt(2, product_id_rs.getInt(1));
//            statement.setInt(3, consumer_id_rs.getInt(1));
//            statement.setDouble(4, order.getAmount());
//
//            statement.executeUpdate();
//
//            return "К заказу\n" + order + "\nдобавлен комментарий:\n" + order.getComment();
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//    }


}
