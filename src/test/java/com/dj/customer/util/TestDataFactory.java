package com.dj.customer.util;

import com.dj.customer.entity.Customer;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Customer validCustomer() {
        return Customer.builder()
                .fullName("Sachin Tendulkar")
                .email("sachin@gmail.com")
                .mobile("9876543210")
                .build();
    }

    public static Customer validCustomer(
            String fullName,
            String email,
            String mobile
    ) {
        return Customer.builder()
                .fullName(fullName)
                .email(email)
                .mobile(mobile)
                .build();
    }

    public static Customer invalidCustomer() {
        return Customer.builder()
                .fullName("")
                .email("invalid-email")
                .mobile("123")
                .build();
    }
}
