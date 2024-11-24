package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.CartItem;

public record CartItemResponse(
        Long id,
        Long productId,
        Integer quantity
) {
    public static CartItemResponse from(CartItem cardItem) {
        return new CartItemResponse(
                cardItem.getId(),
                cardItem.getProduct().getId(),
                cardItem.getQuantity()
        );
    }
}
