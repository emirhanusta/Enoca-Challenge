package Enoca_Challenge.service;

import Enoca_Challenge.dto.response.OrderResponse;
import Enoca_Challenge.exception.custom.*;
import Enoca_Challenge.model.*;
import Enoca_Challenge.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest extends BaseServiceTest{

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldReturnOrderResponse_whenPlaceOrderAndCartIsNotEmpty() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);
        List<CartItem> cartItems = new ArrayList<>();
        Product product = new Product(
                "product",
                BigDecimal.TEN,
                10
        );
        CartItem cartItem = new CartItem(product, 2, BigDecimal.valueOf(20));

        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartService.findByCustomerId(customerId)).thenReturn(cart);

        // act
        OrderResponse result = orderService.placeOrder(customerId);

        // assert
        assertNotNull(result);
    }

    @Test
    void shouldThrowEmptyCartException_whenPlaceOrderAndCartIsEmpty() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);

        when(cartService.findByCustomerId(customerId)).thenReturn(cart);

        // act and assert
        assertThrows(EmptyCartException.class, () -> orderService.placeOrder(customerId));
    }

    @Test
    void shouldThrowInsufficientStockException_whenPlaceOrderAndProductIsOutOfStock() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);
        List<CartItem> cartItems = new ArrayList<>();
        Product product = new Product(
                "product",
                BigDecimal.TEN,
                10
        );
        CartItem cartItem = new CartItem(product, 20, BigDecimal.valueOf(200));

        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartService.findByCustomerId(customerId)).thenReturn(cart);

        // act and assert
        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(customerId));
    }

    @Test
    void shouldThrowProductNotFoundException_whenPlaceOrderAndProductIsDeleted() {
        // arrange
        Long customerId = 1L;
        Cart cart = new Cart();
        Customer customer = new Customer();
        cart.setCustomer(customer);
        List<CartItem> cartItems = new ArrayList<>();
        Product product = new Product(
                "product",
                BigDecimal.TEN,
                10
        );
        product.setDeleted(true);
        CartItem cartItem = new CartItem(product, 2, BigDecimal.valueOf(20));

        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartService.findByCustomerId(customerId)).thenReturn(cart);

        // act and assert
        assertThrows(ProductNotFoundException.class, () -> orderService.placeOrder(customerId));
    }

    @Test
    void shouldReturnOrderResponse_whenGetOrderForCode() {
        // arrange
        String orderCode = "orderCode";
        Customer customer = new Customer();
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderItems(new ArrayList<>());
        when(orderRepository.findByCode(orderCode)).thenReturn(Optional.of(order));

        // act
        OrderResponse result = orderService.getOrderForCode(orderCode);

        // assert
        assertNotNull(result);
    }

    @Test
    void shouldThrowOrderNotFoundException_whenGetOrderForCodeAndOrderNotFound() {
        // arrange
        String orderCode = "orderCode";
        when(orderRepository.findByCode(orderCode)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderForCode(orderCode));
    }

    @Test
    void shouldReturnOrdersForCustomer_whenCustomerExists() {
        // arrange
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        OrderItem orderItem = new OrderItem(
                new Product(),
                "product",
                2,
                BigDecimal.valueOf(20)
        );
        Order order1 = new Order(new Customer(), BigDecimal.valueOf(10), List.of(orderItem));
        Order order2 = new Order(new Customer(), BigDecimal.valueOf(20), List.of(orderItem));
        Page<Order> ordersPage = new PageImpl<>(List.of(order1, order2), pageable, 2);

        when(orderRepository.findAllByCustomerId(customerId, pageable)).thenReturn(ordersPage);

        // act
        Page<OrderResponse> result = orderService.getAllOrdersForCustomer(customerId, pageable);

        // assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(customerService).validateCustomerExists(customerId);
        verify(orderRepository).findAllByCustomerId(customerId, pageable);
    }

    @Test
    void shouldThrowCustomerNotFoundException_whenCustomerDoesNotExist() {
        // arrange
        Long customerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new CustomerNotFoundException("Customer not found")).when(customerService).validateCustomerExists(customerId);

        // act & assert
        assertThrows(CustomerNotFoundException.class, () -> orderService.getAllOrdersForCustomer(customerId, pageable));
        verify(customerService).validateCustomerExists(customerId);
        verifyNoInteractions(orderRepository);
    }
}