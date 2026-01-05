package com.dj.customer.service;

import com.dj.customer.dto.CustomerPatchRequest;
import com.dj.customer.dto.CustomerRequest;
import com.dj.customer.dto.CustomerResponse;
import com.dj.customer.dto.CustomerUpdateRequest;
import com.dj.customer.entity.Customer;
import com.dj.customer.exception.ResourceNotFoundException;
import com.dj.customer.repository.CustomerRepository;

import com.dj.customer.repository.CustomerSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomerService {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public CustomerResponse getById(Long id) {
        log.debug("Service: Fetching customer by id={}", id);

        Customer customer = repository.findById(id).orElseThrow(() -> {
            log.error("Service: Customer not found id={}", id);
            return new ResourceNotFoundException("Customer not found id=" + id);
        });

        log.info("Service: Customer found id={}", id);
        return mapToResponse(customer);
    }

    public CustomerResponse create(CustomerRequest request) {
        log.debug("Service: Creating customer: {}", request.getFullName());

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());

        Customer saved = repository.save(customer);

        log.info("Service: Customer created ID={}", saved.getId());
        return mapToResponse(saved);
    }

    public void delete(Long id) {
        log.debug("Service: Checking if customer exists for delete id={}", id);

        if (!repository.existsById(id)) {
            log.error("Service: Cannot delete. Customer not found id={}", id);
            throw new ResourceNotFoundException("Customer not found id=" + id);
        }

        repository.deleteById(id);

        log.info("Service: Customer deleted id={}", id);
    }

    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        log.debug("Service: Updating customer id={}", id);

        Customer customer = repository.findById(id).orElseThrow(() -> {
            log.error("Service: Cannot update. Customer not found id={}", id);
            return new ResourceNotFoundException("Customer not found id=" + id);
        });

        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setMobile(request.getMobile());

        Customer updated = repository.save(customer);

        log.info("Service: Customer updated successfully id={}", id);
        return mapToResponse(updated);
    }

    public CustomerResponse patch(Long id, CustomerPatchRequest request) {
        log.debug("Service: Partially updating customer id={}", id);

        Customer customer = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service: Cannot patch. Customer not found id={}", id);
                    return new ResourceNotFoundException("Customer not found id=" + id);
                });

        // Partial update logic
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            customer.setFullName(request.getFullName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            customer.setEmail(request.getEmail());
        }

        if (request.getMobile() != null && !request.getMobile().isBlank()) {
            customer.setMobile(request.getMobile());
        }

        Customer updated = repository.save(customer);

        log.info("Service: Customer patched successfully id={}", id);
        return mapToResponse(updated);
    }

    //Search + Pagination
    public Page<CustomerResponse> search(
            String name,
            String email,
            String mobile,
            Pageable pageable) {

        log.debug("Service: Searching customers with filters");

        Specification<Customer> spec = Specification.allOf(
                CustomerSpecification.hasName(name),
                CustomerSpecification.hasEmail(email),
                CustomerSpecification.hasMobile(mobile)
        );

        Page<Customer> page = repository.findAll(spec, pageable);

        log.info("Service: Customers found = {}", page.getTotalElements());

        return page.map(this::mapToResponse);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        log.debug("Service: Mapping Customer entity to CustomerResponse for id={}", customer.getId());

        return CustomerResponse.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .mobile(customer.getMobile())
                .build();
    }
}