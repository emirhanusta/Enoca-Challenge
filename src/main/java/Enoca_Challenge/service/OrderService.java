package Enoca_Challenge.service;

import Enoca_Challenge.dto.response.OrderResponse;
import Enoca_Challenge.exception.custom.EmptyCartException;
import Enoca_Challenge.exception.custom.InsufficientStockException;
import Enoca_Challenge.exception.custom.OrderNotFoundException;
import Enoca_Challenge.exception.custom.ProductNotFoundException;
import Enoca_Challenge.model.*;
import Enoca_Challenge.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, CartService cartService, ProductService productService,
                        CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @Transactional
    public OrderResponse placeOrder(Long customerId) {
        log.info("Placing order for customer with ID: {}", customerId);

        Cart cart = cartService.findByCustomerId(customerId);

        if (cart.getCartItems().isEmpty()) {
            log.error("Customer with ID: {} attempted to place an order with an empty cart.", customerId);
            throw new EmptyCartException("Cart is empty. Please add some products to the cart and try again.");
        }

        log.info("Checking product availability for customer with ID: {}", customerId);
        // Check if the product is deleted
        checkIfProductIsDeleted(cart);

        // Check if the product is in stock
        checkIfStockIsAvailable(cart);

        // Create the order
        Order order = new Order(
                cart.getCustomer(),
                cart.getTotalPrice(),
                cart.getCartItems().stream()
                        .map(cartItem -> new OrderItem(
                                cartItem.getProduct(),
                                cartItem.getProduct().getName(),
                                cartItem.getQuantity(),
                                cartItem.getPriceAtTime()
                        ))
                        .toList()
        );

        log.info("Order created successfully for customer with ID: {}", customerId);
        orderRepository.save(order);
        cartService.emptyCart(customerId);

        return OrderResponse.from(order);
    }

    public Page<OrderResponse> getAllOrdersForCustomer(Long customerId, Pageable pageable) {
        customerService.validateCustomerExists(customerId);
        log.info("Fetching all orders for customer with ID: {}", customerId);
        return orderRepository.findAllByCustomerId(customerId, pageable)
                .map(OrderResponse::from);
    }

    public OrderResponse getOrderForCode(String orderCode) {
        log.info("Fetching order with code: {}", orderCode);
        Order order = orderRepository.findByCode(orderCode)
                .orElseThrow(() -> {
                    log.error("Order with code: {} not found", orderCode);
                    return new OrderNotFoundException("Order with code " + orderCode + " not found.");
                });
        return OrderResponse.from(order);
    }

    private void checkIfProductIsDeleted(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getProduct().isDeleted()) {
                log.error("Product: {} is deleted and cannot be ordered", cartItem.getProduct().getName());
                throw new ProductNotFoundException(cartItem.getProduct().getName() +
                        " is not available for order. Please remove it from the cart and try again.");
            }
        }
    }

    private void checkIfStockIsAvailable(Cart cart) {
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                log.error("Not enough stock for product: {}", product.getName());
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
            productService.reduceProductStock(product, cartItem.getQuantity());
            log.info("Stock reduced for product: {} by quantity: {}", product.getName(), cartItem.getQuantity());
        }
    }
}
