package com.dj.customer.repository;

import com.dj.customer.entity.Customer;
import com.dj.customer.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("Save customer should persist data")
    void saveCustomer_shouldPersist() {
        Customer customer = TestDataFactory.validCustomer();

        Customer saved = customerRepository.save(customer);

        assertNotNull(saved.getId());
        assertEquals("Sachin Tendulkar", saved.getFullName());
    }

    @Test
    @DisplayName("Find by ID should return customer")
    void findById_shouldReturnCustomer() {
        Customer customer = customerRepository.save(
                TestDataFactory.validCustomer(
                        "Rahul Dravid",
                        "rahul@gmail.com",
                        "9999999999"
                )
        );

        Optional<Customer> result = customerRepository.findById(customer.getId());

        assertTrue(result.isPresent());
        assertEquals("Rahul Dravid", result.get().getFullName());
    }

    @Test
    @DisplayName("Find all should return list of customers")
    void findAll_shouldReturnCustomers() {
        customerRepository.save(
                TestDataFactory.validCustomer(
                        "Customer A",
                        "a@gmail.com",
                        "1111111111"
                )
        );

        customerRepository.save(
                TestDataFactory.validCustomer(
                        "Customer B",
                        "b@gmail.com",
                        "2222222222"
                )
        );

        List<Customer> customers = customerRepository.findAll();

        assertEquals(2, customers.size());
    }

    @Test
    @DisplayName("Delete by ID should remove customer")
    void deleteById_shouldDeleteCustomer() {
        Customer customer = customerRepository.save(
                TestDataFactory.validCustomer(
                        "Delete Me",
                        "delete@gmail.com",
                        "0000000000"
                )
        );

        customerRepository.deleteById(customer.getId());

        Optional<Customer> result = customerRepository.findById(customer.getId());
        assertFalse(result.isPresent());
    }
}
