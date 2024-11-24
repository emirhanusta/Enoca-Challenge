package Enoca_Challenge.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart extends BaseEntity {
    @OneToOne
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL)
    private List<CartItem> cartItems = new ArrayList<>();

    private BigDecimal totalPrice;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public BigDecimal  getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal  totalPrice) {
        this.totalPrice = totalPrice;
    }
}
