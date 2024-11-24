package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.Order;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long id,
        Long customerId,
        BigDecimal totalPrice,
        String code,
        List<OrderItemResponse> orderItems
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getTotalPrice(),
                order.getCode(),
                order.getOrderItems().stream().map(OrderItemResponse::from).toList()
        );
    }
}

