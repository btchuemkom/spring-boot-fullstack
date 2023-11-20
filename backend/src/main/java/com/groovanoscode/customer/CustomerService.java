package com.groovanoscode.customer;

import com.groovanoscode.exception.RequestValidationException;
import com.groovanoscode.exception.DuplicateResourceException;
import com.groovanoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final CustomerDTOMapper customerDTOMapper;
    private final PasswordEncoder passwordEncoder;


    // public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
    //    this.customerDao = customerDao;
    //}

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao , CustomerDTOMapper customerDTOMapper , PasswordEncoder passwordEncoder) {
        this.customerDao = customerDao;
        this.customerDTOMapper = customerDTOMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<CustomerDTO> getAllCustomers(){
        return customerDao.selectAllCustomers().stream().map(customerDTOMapper).collect(Collectors.toList());
    }

    public CustomerDTO getCustomer(Integer customerId){
        return customerDao.selectCustomerById(customerId)
                .map(customerDTOMapper)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "customer with [%s] not found".formatted(customerId)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        // check if email exist
        String email = customerRegistrationRequest.email();
        if(customerDao.existsPersonWithEmail(email)){
            throw new DuplicateResourceException(
                    "email already taken");
        }
        // add customer
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                passwordEncoder.encode(customerRegistrationRequest.password()),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(Integer customerId){
        // check if customer with customer_id exist
        if(!customerDao.existsCustomerWithId(customerId)){
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));
        }
        // delete customer with id customer_id
        customerDao.deleteCustomerById(customerId);
    }

    public Customer getCustomerById(Integer customerId){
        return  customerDao.selectCustomerById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId)));
    }

    public void updateCustomerById(Integer customerId, CustomerUpdateRequest request){
        // find the customer with id customerId and throw exception if it does not exist
        Customer customer = customerDao.selectCustomerById(customerId)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "customer with [%s] not found".formatted(customerId)));

        boolean changes = false;

        if(request.name() != null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            changes = true;
        }

        if(request.email() != null && !request.email().equals(customer.getEmail())){
            if(customerDao.existsPersonWithEmail(request.email())){
                throw new DuplicateResourceException("email already taken");
            }
            customer.setEmail(request.email());
            changes = true;
        }

        if(request.age() != null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            changes = true;
        }

        if(request.gender() != null && !request.gender().equals(customer.getGender())){
            customer.setGender(request.gender());
            changes = true;
        }

        // check if there is something to update if not throw a BadRequestException
        if(!changes)
        {
            throw new RequestValidationException("No data changes found");
        }

        // Update the customer data
        customerDao.updateCustomer(customer);
    }

    public void updateGenderOfCustomers() {
        List<Customer> customers = customerDao.selectAllCustomers();
        customers.forEach(customer -> {
            int age = customer.getAge();
            Gender gender = age % 2 == 0 ? Gender.FEMALE : Gender.MALE;
            customer.setGender(gender);
            customerDao.updateCustomer(customer);
        });
    }


}
