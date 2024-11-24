package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal priceAtTime
) {
    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProductName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtTime()
        );
    }
}
