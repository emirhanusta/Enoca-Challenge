package Enoca_Challenge.repository;

import Enoca_Challenge.model.Customer;
import Enoca_Challenge.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

class OrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Test
    void shouldSaveOrder() {
        // arrange
        Order order = new Order();
        // act
        orderRepository.save(order);
        // assert
        Order savedOrder = orderRepository.findById(order.getId()).orElse(null);
        assertNotNull(savedOrder);
    }

    @Test
    void shouldReturnOrdersByCustomerIdAndPageable() {
        // arrange
        Customer customer = new Customer();
        customerRepository.save(customer);
        Order order = new Order(customer, null, null);
        orderRepository.save(order);
        // act
        Page<Order> savedOrder = orderRepository
                .findAllByCustomerId(customer.getId(), Pageable.ofSize(1));
        // assert
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getContent().size(), 1);
    }

    @Test
    void shouldNotReturnOrdersByCustomerIdAndPageable_whenCustomerIdIsNotValid() {
        // arrange
        Customer customer = new Customer();
        customerRepository.save(customer);
        Order order = new Order(customer, null, null);
        orderRepository.save(order);
        // act
        Page<Order> savedOrder = orderRepository
                .findAllByCustomerId(0L, Pageable.ofSize(1));
        // assert
        assertNotNull(savedOrder);
        assertEquals(savedOrder.getContent().size(), 0);
    }

    @Test
    void shouldReturnOrderByCode() {
        // arrange
        Order order = new Order();
        orderRepository.save(order);
        // act
        Order savedOrder = orderRepository.findByCode(order.getCode()).orElse(null);
        // assert
        assertNotNull(savedOrder);
    }

    @Test
    void shouldNotReturnOrderByCode_whenCodeIsNotValid() {
        // arrange
        Order order = new Order();
        orderRepository.save(order);
        // act
        Order savedOrder = orderRepository.findByCode("invalid").orElse(null);
        // assert
        assertNull(savedOrder);
    }
}