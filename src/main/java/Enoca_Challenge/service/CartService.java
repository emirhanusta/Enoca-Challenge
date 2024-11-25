package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.CartItemRequest;
import Enoca_Challenge.dto.response.CartResponse;
import Enoca_Challenge.exception.custom.EmptyCartException;
import Enoca_Challenge.exception.custom.InsufficientStockException;
import Enoca_Challenge.exception.custom.ProductNotFoundException;
import Enoca_Challenge.model.Cart;
import Enoca_Challenge.model.CartItem;
import Enoca_Challenge.model.Product;
import Enoca_Challenge.repository.CartItemRepository;
import Enoca_Challenge.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final CustomerService customerService;
    private final CartItemRepository cartItemRepository;

    public CartService(CartRepository cartRepository, ProductService productService, CustomerService customerService,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.cartItemRepository = cartItemRepository;
    }

    public CartResponse getCart(Long id) {
        log.info("Fetching cart for customer with ID: {}", id);
        Cart cart = getCartOrCreate(id);
        log.info("Cart fetched successfully for customer with ID: {}", id);
        return CartResponse.from(cart);
    }

    public CartResponse addProductToCart(CartItemRequest request) {
        log.info("Adding product with ID: {} to cart for customer with ID: {}", request.productId(), request.customerId());
        Cart cart = getCartOrCreate(request.customerId());
        Product product = productService.getProductById(request.productId());

        CartItem existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId()))
                .findFirst()
                .orElseGet(() -> createNewCardItems(cart, product));

        updateCartItemPrice(existingItem, product, 1);

        checkIfStockIsAvailable(cart);

        updateCartTotal(cart);
        log.info("Product with ID: {} added to cart successfully for customer with ID: {}", request.productId(), request.customerId());

        return CartResponse.from(cartRepository.save(cart));
    }

    public CartResponse reduceProductQuantity(CartItemRequest request) {
        log.info("Removing product with ID: {} from cart for customer with ID: {}", request.productId(), request.customerId());
        Cart cart = findByCustomerId(request.customerId());

        CartItem cartItem = getCartItemByProductId(cart, request.productId());

        if (cartItem.getQuantity() > 1) {
            updateCartItemPrice(cartItem, cartItem.getProduct(), -1);
        } else {
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        updateCartTotal(cart);
        log.info("Product with ID: {} removed from cart successfully for customer with ID: {}", request.productId(), request.customerId());

        return CartResponse.from(cartRepository.save(cart));
    }

    public void emptyCart(Long customerId) {
        log.info("Emptying cart for customer with ID: {}", customerId);
        Cart cart = findByCustomerId(customerId);
        cart.getCartItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        log.info("Cart emptied successfully for customer with ID: {}", customerId);
    }

    public void removeItemFromCart(CartItemRequest request) {
        log.info("Removing item with product ID: {} from cart for customer with ID: {}", request.productId(), request.customerId());
        Cart cart = findByCustomerId(request.customerId());

        CartItem cartItem = getCartItemByProductId(cart, request.productId());

        cart.getCartItems().remove(cartItem);

        updateCartTotal(cart);
        log.info("Item with product ID: {} removed successfully from cart for customer with ID: {}", request.productId(), request.customerId());
    }

    private void checkIfStockIsAvailable(Cart cart) {
        log.debug("Checking stock availability for cart items...");
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                log.error("Insufficient stock for product: {}", product.getName());
                throw new InsufficientStockException("Not enough stock for product: " + product.getName());
            }
        }
        log.info("Stock check completed successfully.");
    }

    private CartItem getCartItemByProductId(Cart cart, Long productId) {
        log.debug("Fetching cart item with product ID: {} from cart.", productId);
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("Product with ID: {} not found in cart!", productId);
                    return new ProductNotFoundException("Product not found in your cart!");
                });
    }

    private void updateCartItemPrice(CartItem cartItem, Product product, int quantityChange) {
        log.debug("Updating cart item price for product ID: {}. Quantity change: {}", product.getId(), quantityChange);
        cartItem.setQuantity(cartItem.getQuantity() + quantityChange);
        cartItem.setPriceAtTime(cartItem.getPriceAtTime().add(product.getPrice().multiply(BigDecimal.valueOf(quantityChange))));
        log.debug("Cart item price updated successfully for product ID: {}", product.getId());
    }

    private void updateCartTotal(Cart cart) {
        log.debug("Updating total price for cart...");
        BigDecimal total = cart.getCartItems().stream()
                .map(CartItem::getPriceAtTime)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        log.debug("Cart total price updated successfully. Total price: {}", total);
    }

    private Cart getCartOrCreate(Long customerId) {
        log.debug("Fetching or creating cart for customer with ID: {}", customerId);
        customerService.validateCustomerExists(customerId);
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    log.info("No existing cart found. Creating a new cart for customer with ID: {}", customerId);
                    return createNewCart(customerId);
                });
    }

    private Cart createNewCart(Long customerId) {
        log.info("Creating new cart for customer with ID: {}", customerId);
        Cart cart = new Cart();
        cart.setCustomer(customerService.getCustomerById(customerId));
        Cart savedCart = cartRepository.save(cart);
        log.info("New cart created successfully for customer with ID: {}", customerId);
        return savedCart;
    }

    protected Cart findByCustomerId(Long customerId) {
        log.debug("Fetching cart for customer with ID: {}", customerId);
        customerService.validateCustomerExists(customerId);
        return cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    log.error("Empty cart for customer with ID: {}", customerId);
                    return new EmptyCartException("Cart is empty for customer with ID: " + customerId);
                });
    }

    private CartItem createNewCardItems(Cart cart, Product product) {
        log.info("Creating new cart item for product ID: {}", product.getId());
        CartItem newItem = new CartItem(
                product,
                0,
                BigDecimal.ZERO
        );
        cart.getCartItems().add(newItem);
        log.info("New cart item created successfully for product ID: {}", product.getId());
        return newItem;
    }
}
