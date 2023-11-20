package com.groovanoscode.customer;

import com.groovanoscode.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    private final JWTUtil jwtUtil;

    public CustomerController(CustomerService customerService , JWTUtil jwtUtil) {
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public List<CustomerDTO> getCustomers(){
        return customerService.getAllCustomers();
    }

    @GetMapping("{customerId}")
    public CustomerDTO getCustomer(@PathVariable("customerId") Integer customerId){
        return customerService.getCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request){
        customerService.addCustomer(request);
        String jwtToken = jwtUtil.issueToken(request.email(), "ROLE_USER"); // We use the email here because it is unique. We cannot have users with the same email
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .build();
    }

    @DeleteMapping("{customer_id}")
    public void deleteCustomerById(@PathVariable("customer_id") Integer customer_id){
        customerService.deleteCustomer(customer_id);
    }

    @PutMapping("{customer_id}")
    public void updateCustomerById(@PathVariable("customer_id") Integer customer_id,
                                   @RequestBody CustomerUpdateRequest request){
        customerService.updateCustomerById(customer_id, request);
    }

    /*@PutMapping("/update/gender")
    public void updateGenderOfCustomers(){
        customerService.updateGenderOfCustomers();
    }*/
}
