package Enoca_Challenge.dto.request;

import Enoca_Challenge.model.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CustomerRequest(
        @NotNull(message = "Name is mandatory")
        String name,
        @Email(message = "Email should be valid")
        @NotNull(message = "Email is mandatory")
        String email) {
    public static Customer from(CustomerRequest customerRequest) {
        return new Customer(customerRequest.name(), customerRequest.email());
    }
}
