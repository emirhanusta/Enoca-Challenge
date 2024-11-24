package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long id,
        Long customerId,
        List<CartItemResponse> cartItems,
        BigDecimal totalPrice
) {
    public static CartResponse from(Cart card) {
        return new CartResponse(
                card.getId(),
                card.getCustomer().getId(),
                card.getCartItems().stream().map(CartItemResponse::from).toList(),
                card.getTotalPrice()
        );
    }
}
