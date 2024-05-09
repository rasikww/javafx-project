package org.example.controller.order;

import org.example.controller.item.ItemController;
import org.example.crudUtil.CrudUtil;
import org.example.db.DBConnection;

import org.example.model.Order;


import java.sql.*;


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
        String lastOrderId = getLastOrderId();
        if (lastOrderId == null) {
            return "D001";
        }
        int newNumber = Integer.parseInt(lastOrderId.substring(1))+1;
        return String.format("D%03d",newNumber);
    }

    private String getLastOrderId() {
        try {
            ResultSet resultSet = CrudUtil.execute("SELECT * FROM orders ORDER BY OrderID DESC LIMIT 1");
            while(resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean placeOrder(Order order) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO orders Value (?, ?, ?)");
            preparedStatement.setString(1, order.getOrderId());
            preparedStatement.setDate(2, Date.valueOf(order.getOrderDate()));
            preparedStatement.setString(3,order.getCustId());

            boolean isOrderAdded = preparedStatement.executeUpdate() > 0;

            boolean isOrderDetailsAdded = OrderDetailController.getInstance().addOrderDetail(order.getOrderDetailList());
            boolean isStockUpdated = ItemController.getInstance().updateStock(order.getOrderDetailList());

            connection.setAutoCommit(true);
            return true;
        } catch (Exception e) {
            try {
                if (connection != null) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
                return false;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
