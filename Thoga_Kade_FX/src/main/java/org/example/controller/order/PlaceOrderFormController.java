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
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.example.controller.customer.CustomerController;
import org.example.controller.item.ItemController;
import org.example.model.Customer;
import org.example.model.Item;
import org.example.model.Order;
import org.example.model.OrderDetail;
import org.example.model.tm.CartTable;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    public JFXButton btnRemoveItem,btnRemoveAll,btnPlaceOrder,btnAddToCart,btnNextOrder;
    public Label lblNetTotal,lblSubTotal,lblItemDesc;
    public Label lblItemPackSize,lblItemUnitPrice,lblItemQuantityAvailable;
    public JFXTextField txtItemQty;
    public Label lblOrderId,lblODCustomerId,lblODCustomerName,lblODDate,lblODTime,lblODDiscount,lblODNetTotal;
    public Label lblCurrentDate,lblCurrentTime,lblCurrentOrderId;
    private ObservableList<CartTable> cartTableList = FXCollections.observableArrayList();
    private Double discount = 0.0;
    private CartTable selectedCartTable = null;
    private Customer selectedCustomer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadComboBoxes();
        clearLblsCmbsTxts();
        loadDateAndTime();
        displayOrderId();
        loadCartTable();
        btnRemoveItem.setDisable(true);
        btnPlaceOrder.setDisable(true);
        tblCart.getFocusModel().focusedIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            if (newIndex != null && newIndex.intValue() >= 0) {
                // Get the selected item at the new focused index
                selectedCartTable = tblCart.getItems().get(newIndex.intValue());
                if (selectedCartTable != null) {
                    showSelectedItemDetails(selectedCartTable);
                    btnRemoveItem.setDisable(false);
                }
            }
        });
    }

    private void showSelectedItemDetails(CartTable selectedCartTable) {
        lblSelectedItemDetails.setText("Selected Item : "+selectedCartTable.getDesc()+" with item code: "+selectedCartTable.getItemCode());
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
        cmbCustomer.getSelectionModel().select(0);
        lblCustomerName.setText(null);
        lblCustomerAddress.setText(null);
        lblCustomerCity.setText(null);
        lblCustomerProvince.setText(null);

        cmbItem.getSelectionModel().select(0);

        cmbItem.setDisable(true);
        lblItemDesc.setText(null);
        lblItemPackSize.setText(null);
        lblItemQuantityAvailable.setText(null);
        lblItemUnitPrice.setText(null);
        txtItemQty.setText(null);
        txtItemQty.setDisable(true);
        btnAddToCart.setDisable(true);

        cmbDiscount.getSelectionModel().select(0);

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
            selectedCustomer = CustomerController.getInstance().searchCustomer(selectedCustomerID);
            lblCustomerName.setText(selectedCustomer.getName());
            lblCustomerAddress.setText(selectedCustomer.getAddress());
            lblCustomerCity.setText(selectedCustomer.getCity());
            lblCustomerProvince.setText(selectedCustomer.getProvince());
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        cmbItem.setDisable(false);
    }

    public void btnRemoveItemOnAction(ActionEvent actionEvent) {
        boolean isRemoved = cartTableList.remove(selectedCartTable);
        if (isRemoved) new Alert(Alert.AlertType.CONFIRMATION,"Item: "+selectedCartTable.getDesc()+" Removed from cart").show();
        else new Alert(Alert.AlertType.ERROR,"Cannot remove item").show();
        lblSubTotal.setText(String.valueOf(calculateTotal()));
        Double netTotal = calculateNetTotal(discount);
        lblNetTotal.setText(String.valueOf(netTotal));
        lblSelectedItemDetails.setText(null);
        btnRemoveItem.setDisable(true);
        if (cartTableList.isEmpty()){
            btnPlaceOrder.setDisable(true);
        }
    }

    public void btnRemoveAllOnAction(ActionEvent actionEvent) {
        cartTableList.clear();
        new Alert(Alert.AlertType.CONFIRMATION,"Removed all items from cart");
        lblSubTotal.setText(String.valueOf(calculateTotal()));
        Double netTotal = calculateNetTotal(discount);
        lblNetTotal.setText(String.valueOf(netTotal));
        lblSelectedItemDetails.setText(null);
        btnPlaceOrder.setDisable(true);
    }

    public void btnPlaceOrderOnAction(ActionEvent actionEvent) {
        cmbItem.setDisable(true);
        txtItemQty.setDisable(true);
        btnAddToCart.setDisable(true);

        tblCart.setDisable(true);
        cmbDiscount.setDisable(true);

        btnRemoveItem.setDisable(true);
        btnRemoveAll.setDisable(true);

        String orderId = lblCurrentOrderId.getText();
        String customerId = selectedCustomer.getId();
        LocalDate date = LocalDate.now();


        lblOrderId.setText(orderId);
        lblODCustomerId.setText(customerId);
        lblODCustomerName.setText(selectedCustomer.getName());
        lblODDate.setText(lblCurrentDate.getText());
        lblODTime.setText(lblCurrentTime.getText());
        lblODDiscount.setText(discount+" %");
        lblODNetTotal.setText(lblNetTotal.getText());

        btnPlaceOrder.setDisable(true);

        List<OrderDetail> orderList = new ArrayList<>();
        for (CartTable row : cartTableList) {
            OrderDetail orderDetail = new OrderDetail(
                    orderId,
                    row.getItemCode(),
                    row.getQty(),
                    discount
            );
            orderList.add(orderDetail);
        }

        Order newOrder = Order.builder()
                .orderId(orderId)
                .orderDate(date)
                .custId(customerId)
                .orderDetailList(orderList)
                .build();

        boolean isOrderPlaced = OrderController.getInstance().placeOrder(newOrder);

        if (isOrderPlaced){
            new Alert(Alert.AlertType.CONFIRMATION,"Order Added Successfully").show();
        }else{
            new Alert(Alert.AlertType.ERROR,"Order was not successfull").show();
        }
    }


    public void cmbDiscountOnAction(ActionEvent actionEvent) {
        discount = Double.parseDouble(cmbDiscount.getSelectionModel().getSelectedItem().split("%")[0]);
        Double netTotal = calculateNetTotal(discount);
        lblNetTotal.setText(String.valueOf(netTotal));
    }

    private Double calculateNetTotal(Double discount) {
        Double subTotal = calculateTotal();
        return subTotal-subTotal*discount/100;
    }

    private Double calculateTotal() {
        Double totalValue = 0.0;
        for (CartTable cartTable : cartTableList) {
            totalValue += cartTable.getTotal();
        }
        return totalValue;
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
        txtItemQty.setText(null);
        txtItemQty.setDisable(false);
        btnAddToCart.setDisable(false);

    }

    public void txtItemQtyOnAction(ActionEvent actionEvent) {
        addToCartBtnProcess();
    }

    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        addToCartBtnProcess();
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

    public void tblViewOnMouseClick(MouseEvent mouseEvent) {
        selectedCartTable = tblCart.getSelectionModel().getSelectedItem();
        lblSelectedItemDetails.setText("Selected Item : "+selectedCartTable.getDesc()+" with item code: "+selectedCartTable.getItemCode());
        btnRemoveItem.setDisable(false);
    }
    private void addToCartBtnProcess(){
        btnRemoveAll.setDisable(false);
        cmbCustomer.setDisable(true);
        String selectedItemCode = cmbItem.getSelectionModel().getSelectedItem().split(" - ")[0];
        Boolean isValidQty = ItemController.getInstance().validateQty(selectedItemCode,txtItemQty.getText());
        if (!isValidQty) {
            new Alert(Alert.AlertType.ERROR, "Please Check Required Quantity").show();
        }else {
            addToCartTable(selectedItemCode,txtItemQty.getText());
            lblSubTotal.setText(String.valueOf(calculateTotal()));
            Double netTotal = calculateNetTotal(discount);
            lblNetTotal.setText(String.valueOf(netTotal));
        }
        if (cartTableList.isEmpty()){
            btnPlaceOrder.setDisable(true);
        }else{
            btnPlaceOrder.setDisable(false);
        }
    }

    public void btnNextOrderOnAction(ActionEvent actionEvent) {

        cartTableList.clear();
        discount = 0.0;
        selectedCartTable = null;

        displayOrderId();
        btnRemoveItem.setDisable(true);
        btnPlaceOrder.setDisable(true);
        cmbCustomer.setDisable(false);
        tblCart.setDisable(false);
        cmbDiscount.setDisable(false);

        clearLblsCmbsTxts();
    }
}
