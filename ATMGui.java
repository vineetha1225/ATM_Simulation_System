import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class ATMGui {
    private Frame frame;
    private TextField usernameField;
    private TextField pinField;
    private TextArea messageArea;
    private Connection connection;

    public ATMGui() {
        // Create the frame
        frame = new Frame("ATM Simulation");
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        // Create components
        Label usernameLabel = new Label("Username:");
        usernameField = new TextField(20);
        Label pinLabel = new Label("PIN:");
        pinField = new TextField(4);
        pinField.setEchoChar('*'); // Mask the PIN input
        Button loginButton = new Button("Login");
        Button checkBalanceButton = new Button("Check Balance");
        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        messageArea = new TextArea(5, 30);
        messageArea.setEditable(false);

        // Add components to the frame
        frame.add(usernameLabel);
        frame.add(usernameField);
        frame.add(pinLabel);
        frame.add(pinField);
        frame.add(loginButton);
        frame.add(checkBalanceButton);
        frame.add(depositButton);
        frame.add(withdrawButton);
        frame.add(messageArea);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        checkBalanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkBalance();
            }
        });

        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });

        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });

        // Set frame properties
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String pin = pinField.getText();

        try {
	    Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish the connection
            String url = "jdbc:mysql://127.0.0.1:3306/ATM"; // Update with your MySQL database URL
            String user = "root"; // Update with your MySQL username
            String password = "vineetha"; // Update with your MySQL password
            connection = DriverManager.getConnection(url, user, password);

            // Prepare and execute the SQL statement
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE username=? AND pin=?");
            statement.setString(1, username);
            statement.setString(2, pin);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                messageArea.setText("Login successful! Welcome, " + username);
            } else {
                messageArea.setText("Invalid username or PIN.");
            }

            // Close the connection
            connection.close();
        } catch (ClassNotFoundException e) {
        	messageArea.setText("JDBC Driver not found: " + e.getMessage());
    	} catch (SQLException ex) {
            messageArea.setText("Connection failed: " + ex.getMessage());
        }
    }

    private void checkBalance() {
        String username = usernameField.getText();
        try {
            // Establish the connection
            String url = "jdbc:mysql://127.0.0.1:3306/ATM"; // Update with your MySQL database URL
            String user = "root"; // Update with your MySQL username
            String password = "vineetha"; // Update with your MySQL password
            connection = DriverManager.getConnection(url, user, password);

            // Prepare and execute the SQL statement
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM users WHERE username=?");
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                messageArea.setText("Your balance is: $" + balance);
            } else {
                messageArea.setText("User  not found.");
            }

            // Close the connection
            connection.close();
        } catch (SQLException ex) {
            messageArea.setText("Connection failed: " + ex.getMessage());
        }
    }

    private void deposit() {
        String username = usernameField.getText();
        String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to deposit:");
        double amount = Double.parseDouble(amountStr);

        try {
            // Establish the connection
            String url = "jdbc:mysql://127.0.0.1:3306/ATM"; // Update with your MySQL database URL
            String user = "root"; // Update with your MySQL username
            String password = "vineetha"; // Update with your MySQL password
            connection = DriverManager.getConnection(url, user, password);

            // Update the balance
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = balance + ? WHERE username=?");
            statement.setDouble(1, amount);
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                messageArea.setText("Successfully deposited: $" + amount);
            } else {
                messageArea.setText("User  not found.");
            }

            // Close the connection
            connection.close();
        } catch (SQLException ex) {
            messageArea.setText("Connection failed: " + ex.getMessage());
        }
    }

    private void withdraw() {
        String username = usernameField.getText();
        String amountStr = JOptionPane.showInputDialog(frame, "Enter amount to withdraw:");
        double amount = Double.parseDouble(amountStr);

        try {
            // Establish the connection
            String url = "jdbc:mysql://127.0.0.1:3306/ATM"; // Update with your MySQL database URL
            String user = "root"; // Update with your MySQL username
            String password = "vineetha"; // Update with your MySQL password
            connection = DriverManager.getConnection(url, user, password);

            // Check if the user has enough balance
            PreparedStatement checkStatement = connection.prepareStatement("SELECT balance FROM users WHERE username=?");
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                if (balance >= amount) {
                    // Update the balance
                    PreparedStatement statement = connection.prepareStatement("UPDATE users SET balance = balance - ? WHERE username=?");
                    statement.setDouble(1, amount);
                    statement.setString(2, username);
                    statement.executeUpdate();
                    messageArea.setText("Successfully withdrew: $" + amount);
                } else {
                    messageArea.setText("Insufficient balance.");
                }
            } else {
                messageArea.setText("User  not found.");
            }

            // Close the connection
            connection.close();
        } catch (SQLException ex) {
            messageArea.setText("Connection failed: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ATMGui();
    }
}