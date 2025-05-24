package com.example.mtc_app.customer.model;

public class CustomerHomePageOrder {
    private String orderId;  // Unique ID from Firestore
    private String status;
    private String dispatchMode;
    private String date;
    private String segment;
    private int price;

    // 🔹 Empty constructor required for Firestore
    public CustomerHomePageOrder() { }

    // 🔹 Constructor for manual object creation
    public CustomerHomePageOrder(String orderId, String status,String segment, String dispatchMode, String date, int price) {
        this.orderId = orderId;
        this.status = status;
        this.segment = segment;
        this.dispatchMode = dispatchMode;
        this.date = date;
        this.price = price;
    }

    // 🔹 Getters
    public String getOrderId() { return orderId; }
    public String getStatus() { return status; }
    public String getDispatchMode() { return dispatchMode; }
    public String getDate() { return date; }
    public String getSegment() { return segment; }
    public int getPrice() { return price; }

    // 🔹 Setters (Required for Firestore)
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public void setStatus(String status) { this.status = status; }
    public void setDispatchMode(String dispatchMode) { this.dispatchMode = dispatchMode; }
    public void setDate(String date) { this.date = date; }
    public void setSegment(String segment) { this.segment = segment; }
    public void setPrice(int price) { this.price = price; }
}
