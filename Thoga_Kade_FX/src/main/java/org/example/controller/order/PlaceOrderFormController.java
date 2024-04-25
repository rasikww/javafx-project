package org.example.controller.order;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.example.controller.customer.CustomerController;
import org.example.controller.item.ItemController;
import org.example.model.Customer;
import org.example.model.Item;
import org.example.model.tm.CartTable;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.Date;

public class PlaceOrderFormController implements Initializable {

    public JFXComboBox<String> cmbCustomer,cmbDiscount,cmbItem;
    public Label lblCustomerName,lblCustomerAddress,lblCustomerCity,lblCustomerProvince;
    public TableView<CartTable> tblCart;
    public TableColumn<CartTable,String> colItemCode,colDesc;
    public TableColumn<CartTable,Double> colUnitPrice;
    public TableColumn<CartTable,Integer> colQty;
    public TableColumn<CartTable,Double> colTotal;
    public Label lblSelectedItemDetails;
    public JFXButton btnRemoveItem,btnRemoveAll,btnPlaceOrder;
    public Label lblNetTotal,lblSubTotal,lblItemDesc;
    public Label lblItemPackSize,lblItemUnitPrice,lblItemQuantityAvailable;
    public JFXTextField txtItemQty;
    public JFXButton btnAddToCart;
    public Label lblOrderId,lblODCustomerId,lblODCustomerName,lblODDate,lblODTime,lblODDiscount,lblODNetTotal;
    public Label lblCurrentDate,lblCurrentTime,lblCurrentOrderId;
    private ObservableList<CartTable> cartTableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadComboBoxes();
        clearLblsCmbsTxts();
        loadDateAndTime();
        displayOrderId();
        loadCartTable();
    }

    private void displayOrderId() {
        String nextOrderId = OrderController.getInstance().generateNextOrderId();
        lblCurrentOrderId.setText(nextOrderId);
    }

    private void loadDateAndTime() {
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("YYYY-MM-dd");
        lblCurrentDate.setText(f.format(date));

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,actionEvent -> {
            LocalTime time = LocalTime.now();
            lblCurrentTime.setText(
                    String.format("%02d : %02d : %02d", time.getHour(), time.getMinute(), time.getSecond())
            );
        }),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void clearLblsCmbsTxts() {
        cmbCustomer.getSelectionModel().clearSelection();
        lblCustomerName.setText(null);
        lblCustomerAddress.setText(null);
        lblCustomerCity.setText(null);
        lblCustomerProvince.setText(null);

        cmbItem.getSelectionModel().clearSelection();
        cmbItem.setDisable(true);
        lblItemDesc.setText(null);
        lblItemPackSize.setText(null);
        lblItemQuantityAvailable.setText(null);
        lblItemUnitPrice.setText(null);
        txtItemQty.setText(null);
        txtItemQty.setDisable(true);
        btnAddToCart.setDisable(true);

        cmbDiscount.getSelectionModel().clearSelection();

        lblSubTotal.setText(null);
        lblNetTotal.setText(null);

        lblSelectedItemDetails.setText(null);

        lblOrderId.setText(null);
        lblODCustomerId.setText(null);
        lblODCustomerName.setText(null);
        lblODDate.setText(null);
        lblODTime.setText(null);
        lblODDiscount.setText(null);
        lblODNetTotal.setText(null);

    }

    private void loadComboBoxes() {
        ObservableList<String> customerIdNames = FXCollections.observableArrayList();
        ObservableList<String> itemIdNames = FXCollections.observableArrayList();
        try {
            ObservableList<Customer> customers = CustomerController.getInstance().getAllCustomers();
            for (Customer customer : customers) {
                customerIdNames.add(customer.toStringIdName());
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            ObservableList<Item> items = ItemController.getInstance().getAllItems();
            for (Item item : items) {
                itemIdNames.add(item.toStringIdName());
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        cmbCustomer.setItems(customerIdNames);
        cmbItem.setItems(itemIdNames);
        ObservableList<String> discounts = FXCollections.observableArrayList("0%", "5%", "10%", "15%", "20%");
        cmbDiscount.setItems(discounts);
    }

    public void cmbCustomerOnAction(ActionEvent actionEvent) {
        String selectedCustomerID = cmbCustomer.getSelectionModel().getSelectedItem().split(" - ")[0];
        try {
            Customer customer = CustomerController.getInstance().searchCustomer(selectedCustomerID);
            lblCustomerName.setText(customer.getName());
            lblCustomerAddress.setText(customer.getAddress());
            lblCustomerCity.setText(customer.getCity());
            lblCustomerProvince.setText(customer.getProvince());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        cmbItem.setDisable(false);
    }

    public void colQtyOnEdit(TableColumn.CellEditEvent cellEditEvent) {
    }

    public void btnRemoveItemOnAction(ActionEvent actionEvent) {
    }

    public void btnRemoveAllOnAction(ActionEvent actionEvent) {
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
    }

    public void cmbDiscountOnAction(ActionEvent actionEvent) {
    }

    public void cmbItemOnAction(ActionEvent actionEvent) {
        String selectedItemCode = cmbItem.getSelectionModel().getSelectedItem().split(" - ")[0];
        try {
            Item item = ItemController.getInstance().searchItem(selectedItemCode);
            lblItemDesc.setText(item.getDesc());
            lblItemPackSize.setText(item.getPackSize());
            lblItemUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            lblItemQuantityAvailable.setText(String.valueOf(item.getQtyOnHand()));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        txtItemQty.setDisable(false);
        btnAddToCart.setDisable(false);
    }

    public void txtItemQtyOnAction(ActionEvent actionEvent) {
    }

    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        cmbCustomer.setDisable(true);
        String selectedItemCode = cmbItem.getSelectionModel().getSelectedItem().split(" - ")[0];
        Boolean isValidQty = ItemController.getInstance().validateQty(selectedItemCode,txtItemQty.getText());
        if (!isValidQty) {
            new Alert(Alert.AlertType.ERROR, "Please Check Required Quantity").show();
        }else {
            addToCartTable(selectedItemCode,txtItemQty.getText());
        }
    }

    private void addToCartTable(String selectedItemCode, String qtyString) {
        try {
            Item item = ItemController.getInstance().searchItem(selectedItemCode);
            CartTable cartTable = new CartTable(
                    item.getItemCode(),
                    item.getDesc(),
                    item.getUnitPrice(),
                    Integer.parseInt(qtyString),
                    item.getUnitPrice()*Integer.parseInt(qtyString)
                    );
            cartTableList.add(cartTable);
            tblCart.setItems(cartTableList);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCartTable(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }
}
