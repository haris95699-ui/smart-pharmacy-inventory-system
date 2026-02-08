import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class InventoryGUI extends JFrame {
    private InventoryManager manager;
    private DefaultTableModel tableModel;
    private JTable inventoryTable;
    private JPanel lowStockPanel; 
    private JLabel totalItemsLabel, lowStockCountLabel, totalValueLabel;
    
    // Color scheme
    private final Color PRIMARY_COLOR = new Color(19,96,107);
    private final Color DANGER_COLOR = new Color(240,22,0);
    private final Color SUCCESS_COLOR = new Color(19,96,107);
    private final Color BACKGROUND_COLOR = new Color(147,177,181);
    private final Color WHITE = Color.WHITE;
    
    public InventoryGUI() {
        manager = new InventoryManager();
        initializeGUI();
        refreshTable();
        checkLowStock();
    }
    
    private void initializeGUI() {
        setTitle("Smart Pharmacy Inventory System - The Boyz");
        setSize(1300, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("📦 Smart Pharmacy Management System");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        JLabel subtitleLabel = new JLabel("Data Structures & Algorithms Project | The Boyz");
        subtitleLabel.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(107, 114, 128));
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(WHITE);
        
        totalItemsLabel = new JLabel("0");
        lowStockCountLabel = new JLabel("0");
        totalValueLabel = new JLabel("Rs 0.00");
        
        statsPanel.add(createStatCard("Total Items", totalItemsLabel, PRIMARY_COLOR));
        lowStockPanel = createStatCard("Low Stock Alert", lowStockCountLabel, PRIMARY_COLOR);
        statsPanel.add(lowStockPanel);
        statsPanel.add(createStatCard("Total Value", totalValueLabel, SUCCESS_COLOR));
        
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Sans Serif", Font.PLAIN, 11));
        titleLabel.setForeground(WHITE);
        
        valueLabel.setFont(new Font("Sans Serif", Font.BOLD, 24));
        valueLabel.setForeground(WHITE);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        String[] columns = {"ID", "Item Name", "Quantity", "Price/Unit", "Total Value", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(new Font("Sans Serif", Font.PLAIN, 13));
        inventoryTable.setRowHeight(40);
        inventoryTable.setGridColor(new Color(229, 231, 235));
        inventoryTable.setSelectionBackground(new Color(219, 234, 254));
        inventoryTable.setSelectionForeground(Color.BLACK);
        
        inventoryTable.getTableHeader().setFont(new Font("Sans Serif", Font.BOLD, 13));
        inventoryTable.getTableHeader().setBackground(new Color(249, 250, 251));
        inventoryTable.getTableHeader().setForeground(new Color(31, 41, 55));
        inventoryTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < inventoryTable.getColumnCount(); i++) {
            inventoryTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        buttonPanel.setBackground(WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(229, 231, 235)));
        
        JButton addBtn = createStyledButton("➕ Add Item", new Color(79,124,130));
        JButton deleteBtn = createStyledButton("🗑️ Delete Item", new Color(79,124,130));
        JButton updateBtn = createStyledButton("✏️ Update Item", new Color(79,124,130));
        JButton searchBtn = createStyledButton("🔍 Search Item", new Color(79,124,130));
        
        // --- SALE BUTTON ---
        JButton saleBtn = createStyledButton("💰 Sale", new Color(79,124,130));
        
        JButton historyBtn = createStyledButton("📜 History", new Color(79,124,130));
        JButton exitBtn = createStyledButton("❌ Exit", new Color(240,22,0));
        
        addBtn.addActionListener(e -> addItem());
        deleteBtn.addActionListener(e -> deleteItem());
        updateBtn.addActionListener(e -> updateItem());
        searchBtn.addActionListener(e -> searchItem());
        
        // --- OPEN THE NEW SALES WINDOW ---
        saleBtn.addActionListener(e -> {
            SalesWindow salesPage = new SalesWindow(manager, this);
            salesPage.setVisible(true);
        });
        
        historyBtn.addActionListener(e -> showHistory());
        exitBtn.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(saleBtn); 
        buttonPanel.add(historyBtn);
        buttonPanel.add(exitBtn);
        
        return buttonPanel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Sans Serif", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(145, 38));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void addItem() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField nameField = new JTextField(20);
        JTextField qtyField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        
        panel.add(new JLabel("Item Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);
        panel.add(new JLabel("Price per Unit (Rs):"));
        panel.add(priceField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Item", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(qtyField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                
                if (name.isEmpty() || quantity < 0 || price < 0) {
                    showError("Invalid input! Please check your values.");
                    return;
                }
                
                manager.addItem(name, quantity, price);
                refreshTable();
                checkLowStock();
                showSuccess("Item added successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for quantity and price!");
            }
        }
    }
    
    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select an item to delete!");
            return;
        }
        
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this item?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (manager.deleteItem(itemId)) {
                refreshTable();
                checkLowStock();
                showSuccess("Item deleted successfully!");
            } else {
                showError("Failed to delete item!");
            }
        }
    }
    
    private void updateItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Please select an item to update!");
            return;
        }
        
        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        Item item = manager.linearSearch(itemId);
        
        if (item != null) {
            JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField qtyField = new JTextField(String.valueOf(item.getQuantity()), 20);
            JTextField priceField = new JTextField(String.valueOf(item.getPricePerUnit()), 20);
            
            panel.add(new JLabel("New Quantity:"));
            panel.add(qtyField);
            panel.add(new JLabel("New Price (Rs):"));
            panel.add(priceField);
            
            int result = JOptionPane.showConfirmDialog(this, panel, 
                "Update Item: " + item.getItemName(), 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int newQty = Integer.parseInt(qtyField.getText().trim());
                    double newPrice = Double.parseDouble(priceField.getText().trim());
                    
                    if (newQty < 0 || newPrice < 0) {
                        showError("Values cannot be negative!");
                        return;
                    }
                    
                    manager.updateItem(itemId, newQty, newPrice);
                    refreshTable();
                    checkLowStock();
                    showSuccess("Item updated successfully!");
                } catch (NumberFormatException ex) {
                    showError("Please enter valid numbers!");
                }
            }
        }
    }
    
    private void searchItem() {
        String[] options = {"Search by ID", "Search by Name"};
        int choice = JOptionPane.showOptionDialog(this, 
            "Choose search method:", "Search Item",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        
        if (choice == 0) {
            String input = JOptionPane.showInputDialog(this, "Enter Item ID:");
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(input.trim());
                    Item item = manager.linearSearch(id);
                    if (item != null) {
                        showItemDetails(item);
                    } else {
                        showWarning("Item not found!");
                    }
                } catch (NumberFormatException ex) {
                    showError("Please enter a valid ID!");
                }
            }
        } else if (choice == 1) {
            String name = JOptionPane.showInputDialog(this, "Enter Item Name:");
            if (name != null && !name.trim().isEmpty()) {
                Item item = manager.searchByName(name.trim());
                if (item != null) {
                    showItemDetails(item);
                } else {
                    showWarning("Item not found!");
                }
            }
        }
    }
    
    private void showItemDetails(Item item) {
        String details = String.format(
            "Item Details:\n\n" +
            "ID: %d\n" +
            "Name: %s\n" +
            "Quantity: %d\n" +
            "Price per Unit: Rs %.2f\n" +
            "Total Value: Rs %.2f\n" +
            "Status: %s",
            item.getItemId(), item.getItemName(), item.getQuantity(),
            item.getPricePerUnit(), item.getTotalValue(),
            item.isLowStock() ? "⚠️ LOW STOCK" : "✅ IN STOCK"
        );
        
        JOptionPane.showMessageDialog(this, details, "Item Found", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHistory() {
        ArrayList<String> history = manager.getHistoryFromDB();
        
        if (history.isEmpty()) {
            showWarning("No operations performed yet!");
            return;
        }
        
        StringBuilder sb = new StringBuilder("Persistent Operation History (Recent First):\n\n");
        
        int count = 1;
        for (String op : history) {
             sb.append(count++).append(". ").append(op).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Sans Serif", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 350));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Operation History", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // --- CHANGED TO PUBLIC SO SalesWindow CAN USE IT ---
    public void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Item> items = manager.getAllItems();
        
        for (Item item : items) {
            String status = item.isLowStock() ? "⚠️ LOW STOCK" : "✅ IN STOCK";
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getItemName(),
                item.getQuantity(),
                String.format("Rs %.2f", item.getPricePerUnit()),
                String.format("Rs %.2f", item.getTotalValue()),
                status
            });
        }
        updateStatistics();
    }
    
    private void updateStatistics() {
        totalItemsLabel.setText(String.valueOf(manager.getSize()));
        
        int lowStockCount = manager.getLowStockItems().size();
        lowStockCountLabel.setText(String.valueOf(lowStockCount));
        
        if (lowStockCount > 0) {
            lowStockPanel.setBackground(DANGER_COLOR);
            lowStockPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DANGER_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
        } else {
            lowStockPanel.setBackground(PRIMARY_COLOR);
            lowStockPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
        }
        
        totalValueLabel.setText(String.format("Rs %.2f", manager.getTotalInventoryValue()));
    }
    
    private void checkLowStock() {
        ArrayList<Item> lowStockItems = manager.getLowStockItems();
        if (!lowStockItems.isEmpty()) {
            StringBuilder message = new StringBuilder("⚠️ LOW STOCK ALERT!\n\n");
            message.append("The following items need restocking:\n\n");
            for (Item item : lowStockItems) {
                message.append(String.format("• %s (ID: %d) - Only %d units left\n",
                    item.getItemName(), item.getItemId(), item.getQuantity()));
            }
            JOptionPane.showMessageDialog(this, message.toString(), 
                "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            InventoryGUI gui = new InventoryGUI();
            gui.setVisible(true);
        });
    }
}