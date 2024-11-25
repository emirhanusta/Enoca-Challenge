package Enoca_Challenge.service;

import Enoca_Challenge.dto.request.CustomerRequest;
import Enoca_Challenge.dto.response.CustomerResponse;
import Enoca_Challenge.exception.custom.CustomerNotFoundException;
import Enoca_Challenge.model.Customer;
import Enoca_Challenge.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CustomerServiceTest extends BaseServiceTest{

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void shouldReturn_CustomerResponse_WhenAddCustomer() {
        // given
        CustomerRequest customerRequest = new CustomerRequest(
                "name",
                "e@e.e"
        );

        Customer customer = CustomerRequest.from(customerRequest);

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // when
        CustomerResponse customerResponse = customerService.addCustomer(customerRequest);

        // then
        assertEquals(customer.getName(), customerResponse.name());
    }

    @Test
    void shouldReturn_Customer_WhenGetCustomerByIdAndCustomerExists() {
        // given
        Long id = 1L;
        Customer customer = new Customer(
                "name",
                "e@e.e"
        );
        customer.setId(id);

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.of(customer));

        // when
        Customer result = customerService.getCustomerById(id);

        // then
        assertEquals(customer, result);
    }

    @Test
    void shouldThrow_CustomerNotFoundException_WhenGetCustomerByIdAndCustomerNotExists() {
        // given
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // when
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(id));

        // then
        assertEquals("Customer with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldThrow_CustomerNotFoundException_WhenValidateCustomerExistsAndCustomerNotExists() {
        // given
        Long id = 1L;

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.empty());

        // when
        CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> customerService.validateCustomerExists(id));

        // then
        assertEquals("Customer with id " + id + " not found", exception.getMessage());
    }

    @Test
    void shouldNotThrow_CustomerNotFoundException_WhenValidateCustomerExistsAndCustomerExists() {
        // given
        Long id = 1L;
        Customer customer = new Customer(
                "name",
                "e@e.e"
        );
        customer.setId(id);

        when(customerRepository.findById(id)).thenReturn(java.util.Optional.of(customer));

        // when
        assertDoesNotThrow(() -> customerService.validateCustomerExists(id));
    }
}