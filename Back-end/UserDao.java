//UserDao.java
package com.sasanka;

import static com.sasanka.DatabaseUtil.close;
import static com.sasanka.DatabaseUtil.getConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    public boolean registerUser(User user) {
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO customers (first_name, last_name, email, password, phone_number, address, postalcode) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getPhone());
            preparedStatement.setString(6, user.getAddress());
            preparedStatement.setInt(7, user.getPostalcode());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(String email, String newPassword) {
        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE customers SET password = ? WHERE email = ?")) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isUserExists(String email) {
        try (Connection connection = DatabaseUtil.getConnection(); 
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM customers WHERE email = ?")) {

            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if there is a user with the given email
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByEmail(String email) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM customers WHERE email = ?")) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    
                    user.setCustomerId(resultSet.getInt("customer_id"));
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setPhone(resultSet.getString("phone_number"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    public boolean isValidUser(String email, String password) throws SQLException {
        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM customers WHERE email = ? AND password = ?")) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if a user with the given email and password is found
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Re-throw the exception to propagate it to the calling method (Servlet)
        }
    }
  public static String getEmailByCustomerId(int customerId) {
        String email = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT email FROM customers WHERE customer_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                email = resultSet.getString("email");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(resultSet, preparedStatement, connection);
        }

        return email;
    }

}