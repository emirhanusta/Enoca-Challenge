package Enoca_Challenge.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order extends BaseEntity {
    @ManyToOne
    private Customer customer;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    private BigDecimal totalPrice;
    private String code;

    public Order(Customer customer, BigDecimal totalPrice, List<OrderItem> orderItems) {
        this.customer = customer;
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }

    public Order() {

    }

    @PrePersist
    public void generateOrderCode() {
        this.code = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public Customer getCustomer() {
        return customer;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getCode() {
        return code;
    }

}

