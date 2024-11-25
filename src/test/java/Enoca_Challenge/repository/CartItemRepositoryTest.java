package Enoca_Challenge.repository;

import Enoca_Challenge.model.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CartItemRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private CartItemRepository cartItemRepository;

    @Test
    void shouldDeleteCartItem() {
        // arrange
        CartItem cartItem = new CartItem();
        cartItemRepository.save(cartItem);

        // act
        cartItemRepository.delete(cartItem);

        // assert
        assertThat(cartItemRepository.findAll().size()).isEqualTo(0);
        assertFalse(cartItemRepository.findById(cartItem.getId()).isPresent());
    }
}