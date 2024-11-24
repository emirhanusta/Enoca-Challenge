package Enoca_Challenge.repository;

import Enoca_Challenge.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveCustomer() {
        Customer customer = new Customer();
        Customer savedCustomer = customerRepository.save(customer);
        assertNotNull(savedCustomer.getId());
    }

    @Test
    void shouldFindCustomerById() {
        Customer customer = new Customer();
        Customer savedCustomer = customerRepository.save(customer);
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        assertNotNull(foundCustomer);
    }
}