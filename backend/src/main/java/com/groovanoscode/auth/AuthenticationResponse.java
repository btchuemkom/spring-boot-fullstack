package com.groovanoscode.auth;

import com.groovanoscode.customer.CustomerDTO;

public record AuthenticationResponse(
        String token,
        CustomerDTO customerDTO
) {
}
