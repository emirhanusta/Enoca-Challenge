package Enoca_Challenge.dto.request;

import Enoca_Challenge.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotNull(message = "Name is mandatory")
        String name,
        @NotNull(message = "Price is mandatory")
        @Min(value = 1, message = "Price must be greater than 0")
        BigDecimal price,
        @NotNull(message = "Stock is mandatory")
        @Min(value = 1, message = "Stock must be greater than 0")
        Integer stock
) {
    public static Product from(ProductRequest productRequest) {
        return new Product(productRequest.name(), productRequest.price(), productRequest.stock());
    }
}
