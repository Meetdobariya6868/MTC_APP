package com.example.mtc_app.customerRepresentative;

public class Customer {
    private String id;  // Firestore document ID
    private String name;
    private String phone;
    private String email;
    private String createdAt;  // Timestamp (String format)

    // No-argument constructor required for Firebase
    public Customer() {
    }

    public Customer(String id, String name, String phone, String email, String createdAt) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    // Setters (needed for Firebase)
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}