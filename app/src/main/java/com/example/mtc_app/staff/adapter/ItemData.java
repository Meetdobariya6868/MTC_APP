package com.example.mtc_app.staff.adapter;

public class ItemData {
    private String title;
    private String subtitle, testSummary, orderStatus, dueDate;
    private int iconResId;
    private String category; // For filtering
    private String documentId; // 🔥 New field for Firestore document reference

    public ItemData(String title, String subtitle, int iconResId, String category, String testSummary, String orderStatus, String documentId, String dueDate) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.category = category;
        this.testSummary = testSummary;
        this.orderStatus = orderStatus;
        this.dueDate = dueDate;
        this.documentId = documentId; // Initialize documentId
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
    public String getDueDate() { return dueDate; }

    public int getIconResId() {
        return iconResId;
    }

    public String getCategory() {
        return category;
    }

    public String getTestSummary() {
        return testSummary;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getDocumentId() {
        return documentId;
    }

    // Optional: Setter for documentId if needed
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
