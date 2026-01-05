package com.dj.customer.integration;

import com.dj.customer.dto.CustomerRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ---------- FULL FLOW: CREATE → GET ----------

    @Test
    @DisplayName("Integration: Create and fetch customer (masked response)")
    void createAndFetchCustomer_endToEnd() throws Exception {

        CustomerRequest request = CustomerRequest.builder()
                .fullName("Sachin Tendulkar")
                .email("sachin@gmail.com")
                .mobile("9876543210")
                .build();

        // CREATE
        String createResponse = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("s****@gmail.com"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(createResponse).get("id").asLong();

        // GET
        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Sachin Tendulkar"))
                .andExpect(jsonPath("$.email").value("s****@gmail.com"))
                .andExpect(jsonPath("$.mobile").value("98******10"))
                .andExpect(header().exists("X-Correlation-Id"));
    }

    // ---------- DELETE FLOW ----------

    @Test
    @DisplayName("Integration: Delete customer")
    void deleteCustomer_endToEnd() throws Exception {

        CustomerRequest request = CustomerRequest.builder()
                .fullName("Delete Me")
                .email("delete@gmail.com")
                .mobile("9999999999")
                .build();

        String response = mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/customers/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/customers/{id}", id))
                .andExpect(status().isNotFound());
    }
}
