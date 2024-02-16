package com.groovanoscode.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class CustomerJPADataAccessService implements CustomerDao{

    private final CustomerRepository customerRepository;

    public CustomerJPADataAccessService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        Page<Customer> page = customerRepository.findAll(Pageable.ofSize(1000));
        return page.getContent();
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public void insertCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customerRepository.existsCustomerByEmail(email);
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public boolean existsCustomerWithId(Integer customer_id) {
        return customerRepository.existsCustomerById(customer_id);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }

}
