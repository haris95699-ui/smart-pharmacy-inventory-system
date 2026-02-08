class Item {
    private int itemId;
    private String itemName;
    private int quantity;
    private double pricePerUnit;
    
    // Constructor
    public Item(int itemId, String itemName, int quantity, double pricePerUnit) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(double pricePerUnit) { this.pricePerUnit = pricePerUnit; }
    
    public double getTotalValue() {
        return quantity * pricePerUnit;
    }
    
    public boolean isLowStock() {
        return quantity < 10;
    }
    
    @Override
    public String toString() {
        return itemId + " | " + itemName + " | Qty: " + quantity + " | Price: Pkr" + pricePerUnit;
    }
}