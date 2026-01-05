package com.dj.customer.controller;

import com.dj.customer.dto.CustomerPatchRequest;
import com.dj.customer.dto.CustomerRequest;
import com.dj.customer.dto.CustomerResponse;
import com.dj.customer.dto.CustomerUpdateRequest;
import com.dj.customer.exception.ResourceNotFoundException;
import com.dj.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService service;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- GET All ----------
    @Test
    @DisplayName("GET /customers with pagination and search")
    void getCustomers_withPaginationAndSearch() throws Exception {

        CustomerResponse response = CustomerResponse.builder()
                .id(1L)
                .fullName("Sachin Tendulkar")
                .email("s****@gmail.com")
                .mobile("98******10")
                .build();

        Page<CustomerResponse> page =
                new PageImpl<>(List.of(response),
                        PageRequest.of(0, 2),
                        1);

        when(service.search(
                eq("Sachin"),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);
        mockMvc.perform(get("/api/v1/customers")
                        .param("name", "Sachin")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "fullName,asc")
                        .header("X-Correlation-Id", "test-corr-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName")
                        .value("Sachin Tendulkar"))
                .andExpect(jsonPath("$.content[0].email")
                        .value("s****@gmail.com"))
                .andExpect(jsonPath("$.totalElements")
                        .value(1));
    }

    // ---------- GET BY ID ----------

    @Test
    @DisplayName("GET /api/v1/customers/{id} - success")
    void getById_shouldReturnCustomer() throws Exception {

        when(service.getById(1L)).thenReturn(
                CustomerResponse.builder()
                        .id(1L)
                        .fullName("Sachin Tendulkar")
                        .email("sachin@gmail.com")
                        .mobile("9876543210")
                        .build()
        );

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("s****@gmail.com"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} - not found")
    void getById_shouldReturn404() throws Exception {

        when(service.getById(1L))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isNotFound());
    }

    // ---------- CREATE ----------

    @Test
    @DisplayName("POST /api/v1/customers - create customer")
    void create_shouldReturn201() throws Exception {

        CustomerRequest request = CustomerRequest.builder()
                .fullName("Sachin Tendulkar")
                .email("sachin@gmail.com")
                .mobile("9876543210")
                .build();

        when(service.create(any(CustomerRequest.class)))
                .thenReturn(
                        CustomerResponse.builder()
                                .id(1L)
                                .fullName("Sachin Tendulkar")
                                .email("sachin@gmail.com")
                                .mobile("9876543210")
                                .build()
                );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    // ---------- PUT ----------
    @Test
    @DisplayName("PUT /api/v1/customers/{id} - update customer")
    void update_shouldReturn200() throws Exception {

        CustomerUpdateRequest request = CustomerUpdateRequest.builder()
                .fullName("Updated Name")
                .email("updated@gmail.com")
                .mobile("9999999999")
                .build();

        when(service.update(eq(1L), any(CustomerUpdateRequest.class)))
                .thenReturn(
                        CustomerResponse.builder()
                                .id(1L)
                                .fullName("Updated Name")
                                .email("updated@gmail.com")
                                .mobile("9999999999")
                                .build()
                );

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Updated Name"));
    }

    // ---------- PATCH ----------

    @Test
    @DisplayName("PATCH /api/v1/customers/{id} - partial update")
    void patch_shouldReturn200() throws Exception {

        CustomerPatchRequest request = CustomerPatchRequest.builder()
                .email("patched@gmail.com")
                .build();

        when(service.patch(eq(1L), any(CustomerPatchRequest.class)))
                .thenReturn(
                        CustomerResponse.builder()
                                .id(1L)
                                .fullName("Sachin Tendulkar")
                                .email("patched@gmail.com")
                                .mobile("9876543210")
                                .build()
                );

        mockMvc.perform(patch("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("p****@gmail.com"));
    }

    // ---------- DELETE ----------

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} - success")
    void delete_shouldReturn204() throws Exception {

        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} - not found")
    void delete_shouldReturn404() throws Exception {

        doThrow(new ResourceNotFoundException("Customer not found"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNotFound());
    }

}
