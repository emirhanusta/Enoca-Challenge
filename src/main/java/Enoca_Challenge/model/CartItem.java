package Enoca_Challenge.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class CartItem extends BaseEntity {
    @ManyToOne
    private Product product;
    private Integer quantity;
    private BigDecimal priceAtTime;

    public CartItem(Product product, Integer quantity, BigDecimal priceAtTime) {
        this.product = product;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    public CartItem() {
    }
    public Product getProduct() {
        return product;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(BigDecimal priceAtTime) {
        this.priceAtTime = priceAtTime;
    }

}
