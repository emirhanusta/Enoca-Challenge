package Enoca_Challenge.controller;

import Enoca_Challenge.dto.response.CustomerResponse;
import Enoca_Challenge.dto.request.CustomerRequest;
import Enoca_Challenge.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer Api v1", description = "It provides the functionality to manage customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @Operation(
            method = "POST",
            summary = "Add a new customer",
            description = "Add a new customer to the system",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Customer added successfully",
                    content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(hidden = true)))
            }
    )
    public ResponseEntity<CustomerResponse> addCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.addCustomer(customerRequest));
    }
}
