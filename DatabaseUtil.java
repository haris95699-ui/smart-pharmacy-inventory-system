import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    
    // --- ⚠️ UPDATE THESE DETAILS ---
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/pharmacy_db";
    private static final String USERNAME = "root"; // Your MySQL username
    private static final String PASSWORD = "haris"; // Your MySQL password
    // ---------------------------------

    public static Connection getConnection() throws SQLException {
        try {
            // Register the driver (no longer needed for modern JDBC, but good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new SQLException("JDBC Driver not found", e);
        }
        
        // Establish and return the connection
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}