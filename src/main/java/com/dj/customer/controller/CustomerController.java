package com.dj.customer.controller;

import com.dj.customer.dto.CustomerPatchRequest;
import com.dj.customer.dto.CustomerRequest;
import com.dj.customer.dto.CustomerResponse;
import com.dj.customer.dto.CustomerUpdateRequest;
import com.dj.customer.service.CustomerService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    //Search+Pagination+Sorting
    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String mobile,
            Pageable pageable) {

        log.info("Controller: Fetching customers with search & pagination");

        return ResponseEntity.ok(
                service.search(name, email, mobile, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable Long id) {

        log.info("Controller: Fetching customer with id={}", id);

        CustomerResponse response = service.getById(id);

        log.info("Controller: Successfully fetched customer id={}", id);
        log.info("Controller: Response = {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {

        log.info("Controller: Creating new customer name={}", request.getFullName());
        log.info("Create customer request = {}", request);

        CustomerResponse response = service.create(request);

        log.info("Controller: Customer created successfully id={}", response.getId());
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        log.info("Controller: Deleting customer id={}", id);

        service.delete(id);

        log.info("Controller: Customer deleted id={}", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {

        log.info("Controller: Updating customer id={} with name={}",
                id, request.getFullName());

        CustomerResponse response = service.update(id, request);

        log.info("Controller: Customer updated successfully id={}", id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerResponse> patch(
            @PathVariable Long id,
            @RequestBody CustomerPatchRequest request) {

        log.info("Controller: Partially updating customer id={}", id);

        CustomerResponse response = service.patch(id, request);

        log.info("Controller: Customer partially updated id={}", id);

        return ResponseEntity.ok(response);
    }
}