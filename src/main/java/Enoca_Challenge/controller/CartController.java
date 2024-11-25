package Enoca_Challenge.controller;

import Enoca_Challenge.dto.request.CartItemRequest;
import Enoca_Challenge.dto.response.CartResponse;
import Enoca_Challenge.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart Api v1", description = "Provides operations for managing carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    @Operation(
            method = "GET",
            summary = "Get the cart of a customer",
            description = "Fetches the cart details for a specific customer by their ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/add")
    @Operation(
            method = "POST",
            summary = "Add a product to the cart",
            description = "Adds a product to the cart based on the provided CartItemRequest.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product added to cart successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addProductToCart(request));
    }

    @PostMapping("/reduce")
    @Operation(
            method = "POST",
            summary = "Reduce the quantity of a product in the cart or remove it",
            description = "Decreases the quantity of a product in the cart or removes it if the quantity becomes zero.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product removed from cart successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found in the cart, or customer not found",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<CartResponse> reduceProductQuantity(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.reduceProductQuantity(request));
    }

    @DeleteMapping("/remove-item")
    @Operation(
            method = "DELETE",
            summary = "Remove a product from the cart",
            description = "Decreases the quantity of a product in the cart or removes it if the quantity becomes zero.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Product removed from cart successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Product not found in the cart",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Void> removeItemFromCart(@Valid @RequestBody CartItemRequest request) {
        cartService.removeItemFromCart(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{customerId}")
    @Operation(
            method = "DELETE",
            summary = "Empty the cart",
            description = "Removes all items from the cart for a specific customer.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cart emptied successfully"),
                    @ApiResponse(responseCode = "404", description = "Customer not found or cart is already empty",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<Void> emptyCart(@PathVariable Long customerId) {
        cartService.emptyCart(customerId);
        return ResponseEntity.noContent().build();
    }
}
