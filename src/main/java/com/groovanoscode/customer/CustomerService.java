package com.groovanoscode.customer;

import com.groovanoscode.exception.RequestValidationException;
import com.groovanoscode.exception.DuplicateResourceException;
import com.groovanoscode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

    // public CustomerService(@Qualifier("jpa") CustomerDao customerDao) {
    //    this.customerDao = customerDao;
    //}

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }

    public Customer getCustomer(Integer customerId){
        return customerDao.selectCustomerById(customerId)
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
                customerRegistrationRequest.age()
        );
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
        Customer customer = getCustomerById(customerId);

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

        // check if there is something to update if not throw a BadRequestException
        if(!changes)
        {
            throw new RequestValidationException("No data changes found");
        }

        // Update the customer data
        customerDao.updateCustomer(customer);
    }
}
