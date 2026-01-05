package com.dj.customer.repository;

import com.dj.customer.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    public static Specification<Customer> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("fullName")),
                                "%" + name.toLowerCase() + "%");
    }

    public static Specification<Customer> hasEmail(String email) {
        return (root, query, cb) ->
                email == null ? null :
                        cb.like(cb.lower(root.get("email")),
                                "%" + email.toLowerCase() + "%");
    }

    public static Specification<Customer> hasMobile(String mobile) {
        return (root, query, cb) ->
                mobile == null ? null :
                        cb.like(root.get("mobile"),
                                "%" + mobile + "%");
    }
}
