package com.example.mtc_app.customerRepresentative;


public class CustomerOrder {
    private String segment;
    private String dispatchMode;
    private String orderDate;
    private String price;
    private String status;

    public CustomerOrder() {}

    public CustomerOrder(String segment, String dispatchMode, String orderDate, String price, String status) {
        this.segment = segment;
        this.dispatchMode = dispatchMode;
        this.orderDate = orderDate;
        this.price = price;
        this.status = status;
    }

    public String getSegment() {
        return segment;
    }

    public String getDispatchMode() {
        return dispatchMode;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public void setDispatchMode(String dispatchMode) {
        this.dispatchMode = dispatchMode;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
