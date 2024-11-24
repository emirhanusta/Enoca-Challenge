package Enoca_Challenge.dto.response;

import Enoca_Challenge.model.Customer;

public record CustomerResponse(
        Long id,
        String name,
        String email) {

    public static CustomerResponse from(Customer savedCustomer) {
        return new CustomerResponse(savedCustomer.getId(), savedCustomer.getName(), savedCustomer.getEmail());
    }
}
