package com.groovanoscode.jwt;

import com.groovanoscode.customer.CustomerUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    private final UserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil , CustomerUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request , @NonNull HttpServletResponse response , @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // If token does not start with "Bearer " throw an error
        if(authHeader == null || !authHeader.startsWith("Bearer ")){ // "Bearer " has 7 characters
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction of the JWT Token
        String jwtToken = authHeader.substring(7); // 7 because "Bearer " has 7 characters

        //get subject from token
        String subject = jwtUtil.getSubject(jwtToken);

        // Load User Details
        if(subject != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // <SecurityContextHolder.getContext().getAuthentication() == null> means that if the user is not authenticated
            UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

            // Check if the token is still valid
            if(jwtUtil.isTokenValid(jwtToken, userDetails.getUsername())){
                //Create the UsernamePasswordAuthenticationToken
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set the Authentication
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }

            filterChain.doFilter(request, response);
        }



    }
}
