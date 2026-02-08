import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

class InventoryManager {

    // Helper to map ResultSet to Item object
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        int id = rs.getInt("itemId");
        String name = rs.getString("itemName");
        int quantity = rs.getInt("quantity");
        double price = rs.getDouble("pricePerUnit");
        return new Item(id, name, quantity, price);
    }

    // Log operations to history
    private void logToDatabase(String message) {
        String sql = "INSERT INTO history_log (message) VALUES (?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, message);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- MODIFIED ADD ITEM METHOD ---
    public boolean addItem(String name, int quantity, double price) {
        // 1. Check if the item already exists by Name
        Item existingItem = searchByNameExact(name);
        
        if (existingItem != null) {
            // ITEM EXISTS: Replace details
            // This updates the existing item with the NEW quantity and NEW price
            boolean success = updateItem(existingItem.getItemId(), quantity, price);
            if (success) {
                 // We manually log this as an "Overwrite" for clarity, or rely on updateItem's log
                 // updateItem already logs "UPDATED...", so we are good.
            }
            return success;
        }

        // 2. ITEM IS NEW: Proceed with Insert
        String sql = "INSERT INTO inventory (itemName, quantity, pricePerUnit) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, quantity);
            pstmt.setDouble(3, price);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        logToDatabase("ADDED: " + name + " (ID: " + newId + ")");
                        return true;
                    }
                }
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Other Methods ---

    public boolean deleteItem(int itemId) {
        Item item = linearSearch(itemId);
        if (item == null) return false;

        String sql = "DELETE FROM inventory WHERE itemId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            if (pstmt.executeUpdate() > 0) {
                logToDatabase("DELETED: " + item.getItemName() + " (ID: " + itemId + ")");
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItem(int itemId, int newQuantity, double newPrice) {
        String sql = "UPDATE inventory SET quantity = ?, pricePerUnit = ? WHERE itemId = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setDouble(2, newPrice);
            pstmt.setInt(3, itemId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                Item item = linearSearch(itemId); 
                if(item != null) {
                    logToDatabase("UPDATED: " + item.getItemName() + " (ID: " + itemId + ")");
                }
                return true;
            }
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Item linearSearch(int itemId) {
        String sql = "SELECT * FROM inventory WHERE itemId = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Used for the fuzzy search in GUI (contains search)
    public Item searchByName(String name) {
        String sql = "SELECT * FROM inventory WHERE itemName LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // NEW: Used for Exact Match (to prevent duplicates during Add Item)
    public Item searchByNameExact(String name) {
        String sql = "SELECT * FROM inventory WHERE itemName = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return mapResultSetToItem(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean sellItem(int itemId, int qtyToSell) {
        Item item = linearSearch(itemId);
        if (item == null) return false; 
        if (item.getQuantity() < qtyToSell) return false; 

        int newQuantity = item.getQuantity() - qtyToSell;
        String sql = "UPDATE inventory SET quantity = ? WHERE itemId = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, itemId);
            
            if (pstmt.executeUpdate() > 0) {
                logToDatabase("SOLD: " + item.getItemName() + " (Qty: " + qtyToSell + ")");
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Item> getAllItems() {
        ArrayList<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM inventory ORDER BY itemId";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) items.add(mapResultSetToItem(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public ArrayList<Item> getLowStockItems() {
        ArrayList<Item> lowStockItems = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE quantity < 10"; 
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) lowStockItems.add(mapResultSetToItem(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lowStockItems;
    }

    public int getSize() {
        String sql = "SELECT COUNT(*) FROM inventory";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalInventoryValue() {
        String sql = "SELECT SUM(quantity * pricePerUnit) FROM inventory";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public ArrayList<String> getHistoryFromDB() {
        ArrayList<String> historyList = new ArrayList<>();
        String sql = "SELECT message, created_at FROM history_log ORDER BY log_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String msg = rs.getString("message");
                String time = rs.getTimestamp("created_at").toString();
                historyList.add("[" + time + "] " + msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyList;
    }
}