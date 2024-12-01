package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.CustomerRequest;
import Enoca_Challenge.dto.response.CustomerResponse;
import Enoca_Challenge.exception.custom.CustomerNotFoundException;
import Enoca_Challenge.model.Customer;
import Enoca_Challenge.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CustomerServiceTest extends BaseServiceTest{

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void shouldReturnCustomerResponse_whenAddCustomer() {
        // arrange
        CustomerRequest customerRequest = new CustomerRequest(
                "name",
                "e@e.e"
        );
        Customer customer = CustomerRequest.from(customerRequest);

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        // act
        CustomerResponse customerResponse = customerService.addCustomer(customerRequest);

        // assert
        assertEquals(customer.getName(), customerResponse.name());
    }

    @Test
    void shouldReturnCustomer_whenGetCustomerByIdAndCustomerExists() {
        // arrange
        Long id = 1L;
        Customer customer = new Customer(
                "name",
                "e@e.e"
        );

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // act
        Customer result = customerService.getCustomerById(id);

        // assert
        assertEquals(customer, result);
    }

    @Test
    void shouldThrowCustomerNotFoundException_whenGetCustomerByIdAndCustomerNotExists() {
        // arrange
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // act
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(id));

        // assert
        assertEquals("Customer with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldThrow_CustomerNotFoundException_WhenValidateCustomerExistsAndCustomerNotExists() {
        // arrange
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // act
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> customerService.validateCustomerExists(id));

        // assert
        assertEquals("Customer with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldNotThrowCustomerNotFoundException_WhenValidateCustomerExistsAndCustomerExists() {
        // arrange
        Long id = 1L;
        Customer customer = new Customer(
                "name",
                "e@e.e"
        );
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // act & assert
        assertDoesNotThrow(() -> customerService.validateCustomerExists(id));
    }
}