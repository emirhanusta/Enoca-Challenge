package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer stock
) {
    public static ProductResponse from(Product savedProduct) {
        return new ProductResponse(savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice(), savedProduct.getStock());
    }
}
