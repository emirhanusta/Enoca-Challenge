package Enoca_Challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class OrderItem extends BaseEntity {
    @ManyToOne
    private Product product;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtTime;

    public OrderItem(Product product,String productName, Integer quantity, BigDecimal priceAtTime) {
        this.product = product;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    public OrderItem() {

    }

    public Product getProduct() {
        return product;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }


    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

}
