package Enoca_Challenge.controller;

import Enoca_Challenge.dto.response.OrderResponse;
import Enoca_Challenge.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API v1", description = "Operations for managing customer orders, including placing, retrieving, and listing orders.")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{customerId}")
    @Operation(
            method = "POST",
            summary = "Place an order",
            description = """
                    Places an order for the customer identified by the provided customer ID.\s
                    This endpoint validates the customer's cart and ensures product availability before placing the order.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Order successfully placed",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request - Possible reasons: cart is empty, or product is out of stock",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Customer or product not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<OrderResponse> placeOrder(@PathVariable Long customerId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.placeOrder(customerId));
    }

    @GetMapping("/list/{customerId}")
    @Operation(
            method = "GET",
            summary = "List all orders for a customer",
            description = """
                    Retrieves a paginated list of all orders placed by the specified customer.\s
                    You can customize the results using query parameters:
                    - `page`: The page number (default: 0).
                    - `size`: The number of records per page (default: 20).
                    - `sort`: Sorting criteria in the format `property,asc|desc`. Examples:
                      - `createdAt,desc` (default)
                      - `totalPrice,asc`
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Orders retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Page.class))
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
    public ResponseEntity<Page<OrderResponse>> getAllOrdersForCustomer(
            @PathVariable Long customerId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrdersForCustomer(customerId, pageable));
    }

    @GetMapping("/{orderCode}")
    @Operation(
            method = "GET",
            summary = "Retrieve an order by its code",
            description = """
                    Fetches the details of a specific order using its unique order code.\s
                    The order code is a unique identifier generated at the time of order placement.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order retrieved successfully",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Order not found - The order code is invalid or does not exist",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<OrderResponse> getOrderForCode(@PathVariable String orderCode) {
        OrderResponse response = orderService.getOrderForCode(orderCode);
        return ResponseEntity.ok(response);
    }
}
