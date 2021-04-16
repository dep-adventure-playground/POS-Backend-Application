package lk.ijse.dep.web.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    private String orderId;
    private String orderDate;
    private String customerId;
    private ArrayList<OrderDetails> orderDetails;

    public Order() {
    }

    public Order(String orderId, String orderDate, String customerId, ArrayList<OrderDetails> orderDetails) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.customerId = customerId;
        this.orderDetails = orderDetails;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public ArrayList<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(ArrayList<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

}
