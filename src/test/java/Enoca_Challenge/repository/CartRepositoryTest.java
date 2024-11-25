package Enoca_Challenge.repository;

import Enoca_Challenge.model.Cart;
import Enoca_Challenge.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CartRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveCart() {
        // arrange
        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        // act
        cartRepository.save(cart);
        // assert
        Optional<Cart> savedCart = cartRepository.findById(cart.getId());
        assertTrue(savedCart.isPresent());
    }

    @Test
    void shouldFindCartByCustomerId() {
        // arrange
        Customer customer = new Customer();
        customerRepository.save(customer);
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);
        // act
        Optional<Cart> savedCart = cartRepository.findByCustomerId(customer.getId());
        // assert
        assertTrue(savedCart.isPresent());
    }

    @Test
    void shouldNotFindCartByCustomerId_whenCartDoesNotExist() {
        // act
        Optional<Cart> savedCart = cartRepository.findByCustomerId(1L);
        // assert
        assertFalse(savedCart.isPresent());
    }
}