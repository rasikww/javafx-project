package org.example.controller.order;

import org.example.db.DBConnection;
import org.example.model.Customer;
import org.example.model.Order;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderController {
    private static OrderController instance;

    private OrderController(){}
    public static OrderController getInstance(){
        if (instance == null) {
            return instance = new OrderController();
        }
        return instance;
    }

    public String generateNextOrderId() {
        String lastOrderId = getLastOrder().getOrderId();
        if (lastOrderId == null) {
            return "D001";
        }
        int newNumber = Integer.parseInt(lastOrderId.substring(1))+1;
        return String.format("D%03d",newNumber);
    }

    private Order getLastOrder() {
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM orders ORDER BY OrderID DESC LIMIT 1");
            while(resultSet.next()) {
                return new Order(
                        resultSet.getString(1),
                        resultSet.getDate(2).toLocalDate(),
                        resultSet.getString(3)
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
