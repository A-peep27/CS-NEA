// this is just an example code from somewhere i claim no credit i just used it as syntax refrnce

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnection {
    // This creates a file database named 'my_database.mv.db' in your project root folder
    private static final String DB_URL = "jdbc:h2:./my_database";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try {
            // 1. Explicitly load the H2 driver (recommended for manual JAR setups)
            Class.forName("org.h2.Driver");
            System.out.println("H2 Driver loaded successfully.");

            // 2. Establish connection and execute queries
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Create a sample table
                stmt.execute("CREATE TABLE IF NOT EXISTS items (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(100), num INT)");

                int number = 5;
                // Insert a record
                stmt.executeUpdate("INSERT INTO items (title, num) VALUES ('item', 5)");

                // Query records
                ResultSet rs = stmt.executeQuery("SELECT id, title, num FROM items");
                System.out.println("\n--- Database Records ---");
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + " | Title: " + rs.getString("title") + " | NUM: " + rs.getInt("num"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}