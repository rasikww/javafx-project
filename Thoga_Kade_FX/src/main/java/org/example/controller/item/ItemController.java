package org.example.controller.item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.crudUtil.CrudUtil;
import org.example.db.DBConnection;
import org.example.model.Item;
import org.example.model.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ItemController {
    private static ItemController instance;

    private ItemController(){}
    public static ItemController getInstance(){
        if (instance == null){
            return instance = new ItemController();
        }
        return instance;
    }

    public ObservableList<Item> getAllItems() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM item");
        ObservableList<Item> allItems = FXCollections.observableArrayList();
        while (resultSet.next()){
                allItems.add(
                        new Item(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3),
                                resultSet.getDouble(4),
                                resultSet.getInt(5)
                        )
                );
            }
            return allItems;
    }

    public Item searchItem(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM item WHERE ItemCode=?");
        preparedStatement.setString(1,itemCode);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            return new Item(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getDouble(4),
                    resultSet.getInt(5)
            );
        }
        return null;
    }

    public Boolean validateQty(String selectedItemCode, String qtyString) {
        try{
            Integer requiredQty = Integer.parseInt(qtyString);
            Item item = searchItem(selectedItemCode);
            if (requiredQty == 0) return false;
            return item.getQtyOnHand() >= requiredQty;
        } catch (NumberFormatException | NullPointerException e ) {
            return false;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateStock(List<OrderDetail> orderDetailList){
        for (OrderDetail orderDetail : orderDetailList) {
            boolean isUpdated = updateStock(orderDetail);
            if (!isUpdated){
                return false;
            }
        }
        return true;
    }
    public boolean updateStock(OrderDetail orderDetail){
        try {
            Object isUpdated = CrudUtil.execute(
                    "UPDATE item SET QtyOnHand=QtyOnHand-? WHERE ItemCode=?",
                    orderDetail.getQty(),
                    orderDetail.getItemCode()
            );
            return (boolean) isUpdated;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}