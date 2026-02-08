import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SalesWindow extends JFrame {
    private InventoryManager manager;
    private InventoryGUI parentGUI;
    private DefaultTableModel tableModel;
    private JTable salesTable;
    private JTextField idField;
    private JLabel grandTotalLabel; 
    
    // --- Color Scheme ---
    private final Color PRIMARY_COLOR = new Color(19,96,107); 
    private final Color BACKGROUND_COLOR = new Color(147,177,181); 
    private final Color WHITE = Color.WHITE;
    private final Color HEADER_TEXT = new Color(31, 41, 55);
    private final Color SUBTITLE_TEXT = new Color(107, 114, 128);
    private final Color DANGER_COLOR = new Color(240,22,0);
    
    public SalesWindow(InventoryManager manager, InventoryGUI parentGUI) {
        this.manager = manager;
        this.parentGUI = parentGUI;
        
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Sales Point - Pharmacy Managment System");
        setSize(1200, 700);
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
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(WHITE);
        
        JLabel titleLabel = new JLabel("💰 Sales Counter");
        titleLabel.setFont(new Font("Sans Serif", Font.BOLD, 28));
        titleLabel.setForeground(HEADER_TEXT);
        
        JLabel subtitleLabel = new JLabel("Manage customer transactions");
        subtitleLabel.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        subtitleLabel.setForeground(SUBTITLE_TEXT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statsPanel.setBackground(WHITE);
        
        grandTotalLabel = new JLabel("0");
        JPanel totalCard = createStatCard("Total Payable", grandTotalLabel, PRIMARY_COLOR);
        totalCard.setPreferredSize(new Dimension(200, 80));
        
        statsPanel.add(totalCard);
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
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        inputPanel.setBackground(WHITE);
        inputPanel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        JLabel scanLabel = new JLabel("Enter ID or Enter Name:");
        scanLabel.setFont(new Font("Sans Serif", Font.BOLD, 13));
        scanLabel.setForeground(HEADER_TEXT);
        
        idField = new JTextField(20);
        idField.setFont(new Font("Sans Serif", Font.PLAIN, 13));
        idField.addActionListener(e -> verifyAndAddItem()); // Allows pressing Enter
        
        JButton addBtn = createStyledButton("Add to List", PRIMARY_COLOR);
        addBtn.setPreferredSize(new Dimension(110, 30));
        addBtn.setFont(new Font("Sans Serif", Font.BOLD, 11));
        addBtn.addActionListener(e -> verifyAndAddItem());
        
        inputPanel.add(scanLabel);
        inputPanel.add(idField);
        inputPanel.add(addBtn);
        
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        
        // --- FIXED: Define clear column headers ---
        String[] columns = {"ID", "Item Description", "Stock", "Price", "Qty", "Total"};
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only 'Qty' (index 4) is editable
            }
        };
        
        // Listener to update total if user edits Qty manually
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                // Check if the change happened in the Qty column (index 4)
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 4) {
                    updateRowCalculations(e.getFirstRow());
                }
            }
        });

        salesTable = new JTable(tableModel);
        salesTable.setFont(new Font("Sans Serif", Font.PLAIN, 13));
        salesTable.setRowHeight(40);
        salesTable.setGridColor(new Color(229, 231, 235));
        salesTable.setSelectionBackground(new Color(219, 234, 254));
        salesTable.setSelectionForeground(Color.BLACK);
        
        salesTable.getTableHeader().setFont(new Font("Sans Serif", Font.BOLD, 13));
        salesTable.getTableHeader().setBackground(new Color(249, 250, 251));
        salesTable.getTableHeader().setForeground(HEADER_TEXT);
        salesTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < salesTable.getColumnCount(); i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        return centerPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 15));
        buttonPanel.setBackground(WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(229, 231, 235)));
        
        JButton saveBtn = createStyledButton("✅ Save Record", PRIMARY_COLOR);
        JButton printBtn = createStyledButton("🖨️ Print Bill", PRIMARY_COLOR);
        JButton deleteBtn = createStyledButton("🗑️ Delete Item", PRIMARY_COLOR); 
        JButton exitBtn = createStyledButton("❌ Close", DANGER_COLOR);
        
        saveBtn.addActionListener(e -> saveRecord());
        printBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Printing Bill... Success!"));
        deleteBtn.addActionListener(e -> deleteItemFromList());
        exitBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(deleteBtn);
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

    // --- LOGIC METHODS ---

    private void verifyAndAddItem() {
        String input = idField.getText().trim();
        if (input.isEmpty()) return;

        Item item = null;

        // 1. Try searching by ID first
        try {
            int id = Integer.parseInt(input);
            item = manager.linearSearch(id);
        } catch (NumberFormatException e) {
            // 2. If not a number, search by Name
            item = manager.searchByName(input);
        }
        
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Item not found (ID or Name)!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Check for Out of Stock
        if (item.getQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Item '" + item.getItemName() + "' is Out of Stock!");
            return;
        }

        // 4. Check if Item is already in the table
        // COLUMN INDEX MAPPING:
        // 0: ID
        // 1: Name
        // 2: Stock
        // 3: Price
        // 4: Qty (Sale Amount)
        // 5: Total
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int existingId = (int) tableModel.getValueAt(i, 0); // Check ID at index 0
            
            if (existingId == item.getItemId()) {
                // ALREADY EXISTS: Increase Quantity by 1
                int currentQty = Integer.parseInt(tableModel.getValueAt(i, 4).toString()); // Qty is index 4
                int stock = Integer.parseInt(tableModel.getValueAt(i, 2).toString()); // Stock is index 2
                
                if (currentQty + 1 > stock) {
                    JOptionPane.showMessageDialog(this, "Cannot add more. Max stock reached!");
                    return;
                }
                
                // Update Qty in table
                tableModel.setValueAt(currentQty + 1, i, 4); // Update Qty at index 4
                // Total will be updated automatically by the TableModelListener
                
                idField.setText("");
                idField.requestFocus();
                return; 
            }
        }
        
        // 5. If NEW to list, Add Row
        // --- FIXED ROW DATA SEQUENCE ---
        tableModel.addRow(new Object[]{
            item.getItemId(),          // Col 0: ID
            item.getItemName(),        // Col 1: Name
            item.getQuantity(),        // Col 2: Stock
            item.getPricePerUnit(),    // Col 3: Price
            1,                         // Col 4: Qty (starts at 1)
            item.getPricePerUnit()     // Col 5: Total (Price * 1)
        });
        
        calculateGrandTotal();
        idField.setText("");
        idField.requestFocus();
    }
    
    private void deleteItemFromList() {
        String input = JOptionPane.showInputDialog(this, "Enter Item ID to remove from list:");
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int idToRemove = Integer.parseInt(input.trim());
                boolean found = false;
                
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int idInTable = (int) tableModel.getValueAt(i, 0); // ID is at index 0
                    if (idInTable == idToRemove) {
                        tableModel.removeRow(i);
                        found = true;
                        calculateGrandTotal(); 
                        JOptionPane.showMessageDialog(this, "Item removed successfully.");
                        break; 
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(this, "ID " + idToRemove + " is not in the list!", "Not Found", JOptionPane.WARNING_MESSAGE);
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid numeric ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateRowCalculations(int row) {
        try {
            // --- FIXED INDEXES HERE ---
            Object qtyObj = tableModel.getValueAt(row, 4);   // Qty is index 4
            Object stockObj = tableModel.getValueAt(row, 2); // Stock is index 2
            Object priceObj = tableModel.getValueAt(row, 3); // Price is index 3
            
            int newQty = Integer.parseInt(qtyObj.toString());
            int currentStock = Integer.parseInt(stockObj.toString());
            double price = Double.parseDouble(priceObj.toString());
            
            // Validation
            if (newQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be > 0");
                SwingUtilities.invokeLater(() -> tableModel.setValueAt(1, row, 4));
                return;
            }
            
            if (newQty > currentStock) {
                JOptionPane.showMessageDialog(this, "Not enough stock! Available: " + currentStock);
                SwingUtilities.invokeLater(() -> tableModel.setValueAt(currentStock, row, 4));
                return;
            }
            
            double newTotal = price * newQty;
            
            // Update Total column (Index 5)
            SwingUtilities.invokeLater(() -> {
                tableModel.setValueAt(newTotal, row, 5);
                calculateGrandTotal();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void calculateGrandTotal() {
        double total = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                // Total is at Index 5
                total += Double.parseDouble(tableModel.getValueAt(i, 5).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        grandTotalLabel.setText(String.format("%.0f", total));
    }

    private void saveRecord() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "List is empty!");
            return;
        }

        boolean allSuccess = true;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int id = (int) tableModel.getValueAt(i, 0); // ID at index 0
            int qty = Integer.parseInt(tableModel.getValueAt(i, 4).toString()); // Qty at index 4
            
            boolean success = manager.sellItem(id, qty);
            if (!success) allSuccess = false;
        }

        if (allSuccess) {
            JOptionPane.showMessageDialog(this, "Transaction Saved Successfully!");
            tableModel.setRowCount(0);
            calculateGrandTotal();
            parentGUI.refreshTable(); 
        } else {
            JOptionPane.showMessageDialog(this, "Error saving some items (Stock may have changed).");
            parentGUI.refreshTable();
        }
    }
}