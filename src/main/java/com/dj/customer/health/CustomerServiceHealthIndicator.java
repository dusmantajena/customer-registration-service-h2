package com.dj.customer.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomerServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {

        return Health.up()
                .withDetail("service", "Customer Registration Service")
                .withDetail("status", "Available")
                .build();
    }
}

