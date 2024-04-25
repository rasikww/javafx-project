package org.example.controller.customer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.example.model.Customer;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerFormController implements Initializable {
    public Tab tabSearch,tabAddCustomer,tabAllCustomerDetails;
    public JFXTextField txtCustomerId,txtName,txtSalary,txtAddress,txtCity,txtProvince,txtPostalCode,txtNextCustomerId;
    public JFXButton btnSearch,btnUpdate,btnDelete,btnAdd,btnRefresh;
    public JFXComboBox<String> cmbTitle;
    public DatePicker datePickerDob;
    public JFXComboBox<String> cmbTitleAdd;
    public JFXTextField txtNameAdd,txtSalaryAdd,txtAddressAdd,txtCityAdd,txtProvinceAdd,txtPostalCodeAdd;
    public DatePicker datePickerDobAdd;
    public TableColumn<Customer,String> colCustomerId,colTitle,colName,colAddress,colCity,colProvince,colPostalCode;
    public TableColumn<Customer,LocalDate> colDob;
    public TableColumn<Customer, Double> colSalary;
    public TableView<Customer> customerTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTable();
        loadComboBoxes();
    }

    private void loadComboBoxes() {
        ObservableList<String> dropDownItems = FXCollections.observableArrayList("Mr", "Miss", "Ms");
        cmbTitle.setItems(dropDownItems);
        cmbTitleAdd.setItems(dropDownItems);
    }

    public void btnSearchOnClick(ActionEvent actionEvent) {
        searchProcess();
    }
    public void searchProcess(){
        String customerId = txtCustomerId.getText();
        Boolean isValidCustomerId = CustomerController.getInstance().validateCustomer(customerId);
        if (isValidCustomerId){
            try {
                Customer searchedCustomer = CustomerController.getInstance().searchCustomer(customerId);
                if (searchedCustomer == null) {
                    new Alert(Alert.AlertType.ERROR,customerId+" Customer does not exist").show();
                    clearCustomerSearchData();
                }else{
                    showCustomerSearchData(searchedCustomer);
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }else{
            new Alert(Alert.AlertType.ERROR,"Enter Valid Customer ID").show();
            clearCustomerSearchData();
        }
    }

    private void showCustomerSearchData(Customer customer) {
        cmbTitle.setValue(customer.getTitle());
        txtName.setText(customer.getName());
        datePickerDob.setValue(customer.getDob());
        txtSalary.setText(String.valueOf(customer.getSalary()));
        txtAddress.setText(customer.getAddress());
        txtCity.setText(customer.getCity());
        txtProvince.setText(customer.getProvince());
        txtPostalCode.setText(customer.getPostalCode());
    }
    private void clearCustomerSearchData(){
        txtCustomerId.setText(null);
        cmbTitle.setValue(null);
        txtName.setText(null);
        datePickerDob.setValue(null);
        txtSalary.setText(null);
        txtAddress.setText(null);
        txtCity.setText(null);
        txtProvince.setText(null);
        txtPostalCode.setText(null);
    }

    public void btnUpdateOnClick(ActionEvent actionEvent) {
        Customer generatedCustomer = generateCustomerFromInfo1();
        if(generatedCustomer!=null){
            boolean isUpdateSuccessful = CustomerController.getInstance().updateCustomer(generatedCustomer);
            if (isUpdateSuccessful) new Alert(Alert.AlertType.CONFIRMATION, "Update Successful").show();
            else new Alert(Alert.AlertType.ERROR, "Update was unsuccessful").show();
        }
    }

    private Customer generateCustomerFromInfo1() {
        if (txtCustomerId.getText().isEmpty() || txtName.getText().isEmpty() || txtSalary.getText().isEmpty() || !txtSalary.getText().matches("^\\+?\\d+(\\.\\d*)?$") || !(CustomerController.getInstance().validateCustomer(txtCustomerId.getText()))) {
            new Alert(Alert.AlertType.ERROR,"Enter Valid Info").show();
            return null;
        }else {
            try {
                return new Customer(
                        txtCustomerId.getText().substring(0,1).toUpperCase()+txtCustomerId.getText().substring(1),
                        cmbTitle.getSelectionModel().getSelectedItem(),
                        txtName.getText(),
                        datePickerDob.getValue(),
                        Double.parseDouble(txtSalary.getText()),
                        txtAddress.getText(),
                        txtCity.getText(),
                        txtProvince.getText(),
                        txtPostalCode.getText()
                );
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,"Invalid Data").show();
                throw new RuntimeException(e);

            }
        }
    }
    private Customer generateCustomerFromInfo2() {
        if (txtNextCustomerId.getText().isEmpty() || txtNameAdd.getText().isEmpty() || txtSalaryAdd.getText().isEmpty() || !txtSalaryAdd.getText().matches("^\\+?\\d+(\\.\\d*)?$") || !(CustomerController.getInstance().validateCustomer(txtNextCustomerId.getText()))) {
            new Alert(Alert.AlertType.ERROR,"Enter Valid Info").show();
            return null;
        }else {
            try {
                return new Customer(
                        txtNextCustomerId.getText().substring(0,1).toUpperCase()+txtNextCustomerId.getText().substring(1),
                        cmbTitleAdd.getSelectionModel().getSelectedItem(),
                        txtNameAdd.getText(),
                        datePickerDobAdd.getValue(),
                        Double.parseDouble(txtSalaryAdd.getText()),
                        txtAddressAdd.getText(),
                        txtCityAdd.getText(),
                        txtProvinceAdd.getText(),
                        txtPostalCodeAdd.getText()
                );
            } catch (NullPointerException|NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR,"Invalid Data").show();
                throw new RuntimeException(e);
            }
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        searchProcess();
        Boolean isDeleted = CustomerController.getInstance().deleteCustomer(txtCustomerId.getText());
        if (isDeleted) new Alert(Alert.AlertType.CONFIRMATION, txtCustomerId.getText() +" Customer Deleted").show();
        else new Alert(Alert.AlertType.ERROR, "Customer Delete Error!").show();
    }

    public void btnAddOnClick(ActionEvent actionEvent) {
        Customer generatedCustomer = generateCustomerFromInfo2();
        if(generatedCustomer!=null){
            try {
                boolean isAddingSuccessful = CustomerController.getInstance().addCustomer(generatedCustomer);
                if (isAddingSuccessful) new Alert(Alert.AlertType.CONFIRMATION, "Customer added Successfully").show();
                else new Alert(Alert.AlertType.ERROR, "Customer was NOT added").show();
            } catch (SQLException | NullPointerException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR,"Please check the values").show();
                throw new RuntimeException(e);
            }
        }
    }

    public void btnRefreshOnClick(ActionEvent actionEvent) {
        loadTable();
    }
    private void loadTable(){
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        try {
            ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();
            customerTable.setItems(allCustomers);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void txtCustomerIdOnKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            searchProcess();
        }
    }

    public void tabAddCustomerOnSelection(Event event) {
        txtNextCustomerId.setText(CustomerController.getInstance().generateNextCustomerId());
    }
}
