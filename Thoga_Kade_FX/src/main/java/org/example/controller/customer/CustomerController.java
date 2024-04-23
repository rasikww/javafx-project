package org.example.controller.customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.db.DBConnection;
import org.example.model.Customer;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerController {
    private static CustomerController instance;

    private CustomerController(){}
    public static CustomerController getInstance(){
        if(instance == null){
            return instance = new CustomerController();
        }
        return instance;
    }

    public ObservableList<Customer> getAllCustomers(){
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM customer");
            ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
            while (resultSet.next()){
                allCustomers.add(
                        new Customer(
                                resultSet.getString("CustID"),
                                resultSet.getString("CustTitle"),
                                resultSet.getString("CustName"),
                                resultSet.getDate("DOB").toLocalDate(),
                                resultSet.getDouble("salary"),
                                resultSet.getString("CustAddress"),
                                resultSet.getString("City"),
                                resultSet.getString("Province"),
                                resultSet.getString("PostalCode")
                        )
                );
            }
            return allCustomers;
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public Customer searchCustomer(String id){
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM customer WHERE CustID='" + id +"'");
            while (resultSet.next()){
                return new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9)
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Boolean validateCustomer(String customerId) {
        Pattern p=Pattern.compile("[Cc]\\d{3,4}");
        Matcher m=p.matcher(customerId);
        return m.matches();
    }
    public String generateNextCustomerId(){
        String lastCustomerId = getLastCustomer().getId();
        int newNumber = Integer.parseInt(lastCustomerId.substring(1))+1;
        return String.format("C%03d",newNumber);
    }

    private Customer getLastCustomer() {
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM customer ORDER BY custid DESC LIMIT 1");
            while(resultSet.next()){
                return new Customer(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDate(4).toLocalDate(),
                        resultSet.getDouble(5),
                        resultSet.getString(6),
                        resultSet.getString(7),
                        resultSet.getString(8),
                        resultSet.getString(9)
                );
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public boolean updateCustomer(Customer customer) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("UPDATE customer SET " +
                    "CustTitle = ?," +
                    "CustName = ?," +
                    "DOB = ?," +
                    "salary = ?," +
                    "CustAddress = ?," +
                    "City = ?," +
                    "Province = ?," +
                    "PostalCode = ?" +
                    "WHERE CustID = ?");
            preparedStatement.setString(1,customer.getTitle());
            preparedStatement.setString(2,customer.getName());
            preparedStatement.setDate(3, Date.valueOf(customer.getDob()));
            preparedStatement.setDouble(4,customer.getSalary());
            preparedStatement.setString(5,customer.getAddress());
            preparedStatement.setString(6,customer.getCity());
            preparedStatement.setString(7,customer.getProvince());
            preparedStatement.setString(8,customer.getPostalCode());
            preparedStatement.setString(9,customer.getId());
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
