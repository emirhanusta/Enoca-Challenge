package Enoca_Challenge.model;

import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
public class Product extends BaseEntity {

    private String name;
    private BigDecimal  price;
    private Integer stock;
    private boolean isDeleted = false;

    public Product(String name, BigDecimal  price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Product() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal  price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
