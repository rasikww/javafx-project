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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.example.model.Customer;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class CustomerFormController implements Initializable {
    public Tab tabSearch,tabAddCustomer,tabAllCustomerDetails;
    public JFXTextField txtCustomerId,txtName,txtSalary,txtAddress,txtCity,txtProvince,txtPostalCode,txtNextCustomerId;
    public JFXButton btnSearch,btnUpdate,btnDelete,btnAdd,btnRefresh;
    public JFXComboBox cmbTitle;
    public DatePicker datePickerDob;
    public JFXComboBox cmbTitleAdd;
    public JFXTextField txtNameAdd,txtSalaryAdd,txtAddressAdd,txtCityAdd,txtProvinceAdd,txtPostalCodeAdd;
    public DatePicker datePickerDobAdd;
    public TableColumn colCustomerId,colTitle,colName,colDob,colSalary,colAddress,colCity,colProvince,colPostalCode;
    public TableView customerTable;

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
            Customer searchedCustomer = CustomerController.getInstance().searchCustomer(customerId);
            if (searchedCustomer == null) {
                new Alert(Alert.AlertType.ERROR,customerId+" Customer does not exist").show();
                clearCustomerSearchData();
            }else{
                showCustomerSearchData(searchedCustomer);
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
        Customer generatedCustomer = generateCustomerFromInfo();
        if(generatedCustomer!=null){
            boolean isUpdateSuccessful = CustomerController.getInstance().updateCustomer(generatedCustomer);
            if (isUpdateSuccessful) new Alert(Alert.AlertType.CONFIRMATION, "Update Succesful").show();
            else new Alert(Alert.AlertType.ERROR, "Update was unsuccessful").show();
        }
    }

    private Customer generateCustomerFromInfo() {
        if (txtCustomerId.getText().isEmpty() || txtName.getText().isEmpty() || txtSalary.getText().isEmpty() || !txtSalary.getText().matches("^\\+?\\d+(\\.\\d*)?$") || !(CustomerController.getInstance().validateCustomer(txtCustomerId.getText()))) {
            new Alert(Alert.AlertType.ERROR,"Enter Valid Info").show();
            return null;
        }else {
            try {
                return new Customer(
                        txtCustomerId.getText(),
                        cmbTitle.getSelectionModel().getSelectedItem().toString(),
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

    public void btnDeleteOnAction(ActionEvent actionEvent) {
    }

    public void btnAddOnClick(ActionEvent actionEvent) {
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

        ObservableList<Customer> allCustomers = CustomerController.getInstance().getAllCustomers();
        customerTable.setItems(allCustomers);
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
