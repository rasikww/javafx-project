package org.example.controller.order;

import org.example.crudUtil.CrudUtil;
import org.example.model.OrderDetail;

import java.sql.SQLException;
import java.util.List;

public class OrderDetailController {
    private static OrderDetailController instance;

    private OrderDetailController(){}

    public static OrderDetailController getInstance(){
        if (instance == null) return instance = new OrderDetailController();
        return instance;
    }

    public boolean addOrderDetail(List<OrderDetail> orderDetailList) {
        for (OrderDetail orderDetail : orderDetailList) {
            boolean isAdded = addOrderDetail(orderDetail);
            if (!isAdded) return false;
        }
        return true;
    }
    private boolean addOrderDetail(OrderDetail orderDetail){
        try {
            Object isAdded = CrudUtil.execute(
                    "INSERT INTO orderdetail VALUES (?, ?, ?, ?)",
                    orderDetail.getOrderId(),
                    orderDetail.getItemCode(),
                    orderDetail.getQty(),
                    orderDetail.getDiscount()
            );
            return (boolean) isAdded;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
