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
@Tag(name = "Cart API v1", description = "Endpoints for managing customer carts, including adding, removing, and fetching cart items.")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{customerId}")
    @Operation(
            method = "GET",
            summary = "Retrieve customer cart",
            description = """
                    Fetches the cart details for a specific customer identified by their ID.\s
                    This includes all items currently in the cart along with their quantities and total price.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Cart retrieved successfully",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Cart is empty",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/add")
    @Operation(
            method = "POST",
            summary = "Add a product to the cart",
            description = """
                    Adds a specified product to the cart or increases its quantity if it already exists in the cart.\s
                    The request should include the product ID, customer ID, and desired quantity.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product successfully added to the cart",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request, product out of stock, or insufficient stock available",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<CartResponse> addProductToCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addProductToCart(request));
    }

    @PostMapping("/reduce")
    @Operation(
            method = "POST",
            summary = "Reduce product quantity in the cart",
            description = """
                    Reduces the quantity of a specified product in the cart.\s
                    If the quantity reaches zero, the product is removed from the cart.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product quantity reduced or removed from the cart",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found in the cart or customer does not exist",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<CartResponse> reduceProductQuantity(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.reduceProductQuantity(request));
    }

    @DeleteMapping("/remove-item")
    @Operation(
            method = "DELETE",
            summary = "Remove a product from the cart",
            description = """
                    Completely removes a specified product from the cart, regardless of its quantity.
                    The request must specify the customer ID and product ID.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product successfully removed from the cart",
                            content = @Content(schema = @Schema(implementation = CartResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found in the cart or customer not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
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
            description = """
                    Removes all items from the cart for a specific customer.\s
                    Use this endpoint to clear the cart entirely for a given customer ID.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Cart successfully emptied"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Cart is already empty",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<Void> emptyCart(@PathVariable Long customerId) {
        cartService.emptyCart(customerId);
        return ResponseEntity.noContent().build();
    }
}

