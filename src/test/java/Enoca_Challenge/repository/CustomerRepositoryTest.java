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
        // arrange
        Customer customer = new Customer();
        // act
        Customer savedCustomer = customerRepository.save(customer);
        // assert
        assertNotNull(savedCustomer.getId());
    }

    @Test
    void shouldFindCustomerById() {
        // arrange
        Customer customer = new Customer();
        Customer savedCustomer = customerRepository.save(customer);
        // act
        Customer foundCustomer = customerRepository.findById(savedCustomer.getId()).orElse(null);
        // assert
        assertNotNull(foundCustomer);
    }

    @Test
    void shouldNotFindCustomerById() {
        // arrange
        Long id = 1L;
        // act
        Customer foundCustomer = customerRepository.findById(id).orElse(null);
        // assert
        assertNull(foundCustomer);
    }
}