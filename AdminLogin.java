import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLogin extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLogin() {
        // Frame settings (unchanged)
        setTitle("Pharmacy Managment System - Admin Login");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background image (unchanged)
        ImageIcon bgIcon = new ImageIcon("pharmacy.jpg"); // put your image
        Image img = bgIcon.getImage().getScaledInstance(600, 400, Image.SCALE_SMOOTH);
        bgIcon = new ImageIcon(img);

        JLabel background = new JLabel(bgIcon);
        background.setLayout(new GridBagLayout());
        add(background);

        // Transparent login panel (unchanged)
        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridLayout(4, 1, 10, 10));
        loginPanel.setPreferredSize(new Dimension(300, 200));
 
        JLabel titleLabel = new JLabel("Pharmacy Managment System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255,255,255));

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(0,0,0));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);

        // --- MODIFIED: Login button action ---
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login Successful! Welcome " + username + ".");
                    dispose();  // Closes the login window
    
                    // Create and show the main inventory window
                    InventoryGUI inventoryApp = new InventoryGUI();
                    inventoryApp.setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username or Password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        // --- END OF MODIFICATION ---

        // Adding components (unchanged)
        loginPanel.add(titleLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);

        background.add(loginPanel);

        setVisible(true);
    }
    
    // --- NEW METHOD: Validates credentials against the database ---
    private boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        // Note: Storing plain-text passwords is insecure.
        // For a real app, you would "hash" the password.
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If a record is found, rs.next() is true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // --- END OF NEW METHOD ---

    // Main method (unchanged)
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AdminLogin();
            }
        });
    }
}