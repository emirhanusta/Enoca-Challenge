package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.CartItemRequest;
import Enoca_Challenge.dto.response.CartResponse;
import Enoca_Challenge.exception.custom.EmptyCartException;
import Enoca_Challenge.exception.custom.InsufficientStockException;
import Enoca_Challenge.exception.custom.ProductNotFoundException;
import Enoca_Challenge.model.Cart;
import Enoca_Challenge.model.CartItem;
import Enoca_Challenge.model.Customer;
import Enoca_Challenge.model.Product;
import Enoca_Challenge.repository.CartItemRepository;
import Enoca_Challenge.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CartServiceTest extends BaseServiceTest{

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductService productService;

    @Mock
    private CustomerService customerService;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldReturnCartResponse_whenGetCartAndCartExists() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        // act
        CartResponse result = cartService.getCart(customerId);

        // assert
        assertNotNull(result);
    }

    @Test
    void shouldCreateNewCart_whenGetCartAndCartDoesNotExist() {
        // arrange
        Long customerId = 1L;
        Customer customer = new Customer();
        customer.setId(customerId);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());
        when(customerService.getCustomerById(customerId)).thenReturn(customer);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        CartResponse result = cartService.getCart(customerId);

        // assert
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void shouldReturnCartResponse_whenAddProductToCart() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);
        Product product = new Product(
                "product name",
                BigDecimal.TEN,
                10
        );
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(productService.getProductById(productId)).thenReturn(product);


        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // act
        CartResponse result = cartService.addProductToCart(request);

        // assert
        assertNotNull(result);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(BigDecimal.TEN, cart.getTotalPrice());
    }

    @Test
    void shouldThrowInsufficientStockException_whenAddProductToCartAndStockIsNotAvailable() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);

        Product product = new Product(
                "product name",
                BigDecimal.TEN,
                0
        );

        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(productService.getProductById(productId)).thenReturn(product);

        // act
        Exception exception = assertThrows(InsufficientStockException.class, () -> cartService.addProductToCart(request));

        // assert
        assertEquals("Not enough stock for product: " + product.getName(), exception.getMessage());
    }

    @Test
    void shouldReduceProductQuantity_whenCartItemQuantityIsGreaterThanOne() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);

        Product product = new Product("Product Name", BigDecimal.TEN, 10);
        product.setId(productId);
        CartItem cartItem = new CartItem(product, 2, BigDecimal.valueOf(20));
        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        cart.setCartItems(List.of(cartItem));

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // act
        CartResponse result = cartService.reduceProductQuantity(request);

        // assert
        assertNotNull(result);
        assertEquals(1, cartItem.getQuantity());
        assertEquals(BigDecimal.valueOf(10), cart.getTotalPrice());
        verify(cartItemRepository, never()).delete(cartItem);
    }

    @Test
    void shouldRemoveCartItem_whenCartItemQuantityIsOne() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);

        Product product = new Product("Product Name", BigDecimal.TEN, 10);
        product.setId(productId);
        CartItem cartItem = new CartItem(product, 1, BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // act
        CartResponse result = cartService.reduceProductQuantity(request);

        // assert
        assertNotNull(result);
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void shouldThrowProductNotFoundException_whenProductIsNotInCart() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);

        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        cart.setCartItems(new ArrayList<>());

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        // act & assert
        assertThrows(ProductNotFoundException.class, () -> cartService.reduceProductQuantity(request));
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void shouldThrowEmptyCartException_whenCartIsEmpty() {
        // arrange
        Long customerId = 1L;
        CartItemRequest request = new CartItemRequest(1L, customerId);

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.empty());

        // act & assert
        assertThrows(EmptyCartException.class, () -> cartService.reduceProductQuantity(request));
        verify(cartItemRepository, never()).delete(any());
    }

    @Test
    void shouldEmptyCart_whenEmptyCart() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        cart.setCartItems(new ArrayList<>());

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);

        // act
        cartService.emptyCart(customerId);

        // assert
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
    }

    @Test
    void shouldRemoveItemFromCart_whenRemoveItemFromCart() {
        // arrange
        Long customerId = 1L;
        Long productId = 1L;
        CartItemRequest request = new CartItemRequest(productId, customerId);

        Product product = new Product("Product Name", BigDecimal.TEN, 10);
        product.setId(productId);
        CartItem cartItem = new CartItem(product, 1, BigDecimal.TEN);
        Cart cart = new Cart();
        cart.setCustomer(new Customer());
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));

        // act
        cartService.removeItemFromCart(request);

        // assert
        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
    }
}
