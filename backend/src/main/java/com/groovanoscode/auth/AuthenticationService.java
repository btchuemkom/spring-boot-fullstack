package com.groovanoscode.auth;

import com.groovanoscode.customer.Customer;
import com.groovanoscode.customer.CustomerDTO;
import com.groovanoscode.customer.CustomerDTOMapper;
import com.groovanoscode.jwt.JWTUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final CustomerDTOMapper customerDTOMapper;
    private final JWTUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager , CustomerDTOMapper customerDTOMapper , JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.customerDTOMapper = customerDTOMapper;
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResponse login(AuthenticationRequest request){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username() ,
                        request.password()
                )
        );

        Customer principal = (Customer)authentication.getPrincipal(); // This return a customer object
        CustomerDTO customerDTO = customerDTOMapper.apply(principal);
        String jwtToken = jwtUtil.issueToken(customerDTO.username(), customerDTO.roles());

        return new AuthenticationResponse(jwtToken, customerDTO);
    }

}
