package com.dj.customer.service;

import com.dj.customer.dto.CustomerPatchRequest;
import com.dj.customer.dto.CustomerRequest;
import com.dj.customer.dto.CustomerResponse;
import com.dj.customer.dto.CustomerUpdateRequest;
import com.dj.customer.entity.Customer;
import com.dj.customer.exception.ResourceNotFoundException;
import com.dj.customer.repository.CustomerRepository;
import com.dj.customer.util.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService customerService;

    // ---------- GET BY ID ----------
    @Test
    @DisplayName("Get customer by ID - success")
    void getById_shouldReturnCustomer() {
        Customer customer = TestDataFactory.validCustomer();
        customer.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponse response = customerService.getById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Sachin Tendulkar", response.getFullName());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Get customer by ID - not found")
    void getById_shouldThrowException_whenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getById(1L)
        );

        verify(repository).findById(1L);
    }

    // ---------- CREATE ----------

    @Test
    @DisplayName("Create customer - success")
    void create_shouldSaveCustomer() {
        Customer savedCustomer = TestDataFactory.validCustomer();
        savedCustomer.setId(1L);

        when(repository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerRequest request = CustomerRequest.builder()
                .fullName("Sachin Tendulkar")
                .email("sachin@gmail.com")
                .mobile("9876543210")
                .build();

        CustomerResponse response = customerService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(repository).save(any(Customer.class));
    }

    // ---------- DELETE ----------

    @Test
    @DisplayName("Delete customer - success")
    void delete_shouldRemoveCustomer() {
        when(repository.existsById(1L)).thenReturn(true);

        customerService.delete(1L);

        verify(repository).existsById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete customer - not found")
    void delete_shouldThrowException_whenNotFound() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.delete(1L)
        );

        verify(repository).existsById(1L);
        verify(repository, never()).deleteById(anyLong());
    }

    // ---------- UPDATE (PUT) ----------

    @Test
    @DisplayName("Update customer - success")
    void update_shouldUpdateCustomer() {
        Customer existing = TestDataFactory.validCustomer();
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenReturn(existing);

        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .fullName("Updated Name")
                .email("updated@gmail.com")
                .mobile("8888888888")
                .build();

        CustomerResponse response = customerService.update(1L, request);

        assertEquals("Updated Name", response.getFullName());
        verify(repository).findById(1L);
        verify(repository).save(any(Customer.class));
    }

    // ---------- PATCH ----------

    @Test
    @DisplayName("Patch customer - partial update")
    void patch_shouldUpdateOnlyProvidedFields() {
        Customer existing = TestDataFactory.validCustomer();
        existing.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Customer.class))).thenReturn(existing);

        CustomerPatchRequest request = CustomerPatchRequest.builder()
                .email("patched@gmail.com")
                .build();

        CustomerResponse response = customerService.patch(1L, request);

        assertEquals("patched@gmail.com", response.getEmail());
        assertEquals("Sachin Tendulkar", response.getFullName()); // unchanged

        verify(repository).findById(1L);
        verify(repository).save(any(Customer.class));
    }

    @Test
    void search_shouldReturnPagedCustomers() {

        Pageable pageable = PageRequest.of(0, 2, Sort.by("fullName").ascending());

        Customer customer = Customer.builder()
                .id(1L)
                .fullName("Sachin Tendulkar")
                .email("sachin@gmail.com")
                .mobile("9876543210")
                .build();

        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(repository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        Page<CustomerResponse> result =
                customerService.search("Sachin", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Sachin Tendulkar",
                result.getContent().get(0).getFullName());

        verify(repository).findAll(any(Specification.class), eq(pageable));
    }
}
