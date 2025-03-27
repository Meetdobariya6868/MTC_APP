package com.example.mtc_app.staff.adapter;

public class ItemData {
    private String title;
    private String subtitle, testSummary;
    private int iconResId;
    private String category; // New field for filtering

    public ItemData(String title, String subtitle, int iconResId, String category, String testSummary) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.category = category;
        this.testSummary = testSummary;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getCategory() {
        return category;
    }

    public String getTestSummary() {
        return testSummary;
    }
}