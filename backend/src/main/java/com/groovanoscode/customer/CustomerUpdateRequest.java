package com.groovanoscode.customer;

public record CustomerUpdateRequest(
        String name,
        String email,
        Integer age
) {
}
