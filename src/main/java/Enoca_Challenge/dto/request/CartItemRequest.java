package Enoca_Challenge.dto.request;

import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull(message = "Customer id is mandatory")
        Long customerId,
        @NotNull(message = "Product id is mandatory")
        Long productId
) {
}
