package com.groovanoscode.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
