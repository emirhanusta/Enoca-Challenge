package Enoca_Challenge.service;

import Enoca_Challenge.dto.response.CustomerResponse;
import Enoca_Challenge.dto.request.CustomerRequest;
import Enoca_Challenge.exception.custom.CustomerNotFoundException;
import Enoca_Challenge.model.Customer;
import Enoca_Challenge.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse addCustomer(CustomerRequest customerRequest) {
        log.info("Adding new customer with details: {}", customerRequest);
        Customer savedCustomer = customerRepository.save(CustomerRequest.from(customerRequest));
        log.info("Customer with id {} successfully added", savedCustomer.getId());
        return CustomerResponse.from(savedCustomer);
    }

    protected Customer getCustomerById(Long id) {
        return findCustomer(id).orElseThrow(
                () -> {
                    log.error("Customer with id {} not found", id);
                    return new CustomerNotFoundException("Customer with id " + id + " not found");
                }
        );
    }

    protected void validateCustomerExists(Long id) {
        if (findCustomer(id).isEmpty()) {
            log.error("Customer with id {} not found", id);
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
    }

    private Optional<Customer> findCustomer(Long id) {
        log.debug("Fetching customer with id {}", id);
        return customerRepository.findById(id);
    }
}
