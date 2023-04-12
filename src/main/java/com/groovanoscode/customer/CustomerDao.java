package com.groovanoscode.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();

    Optional<Customer> selectCustomerById(Integer customerId);

    void insertCustomer(Customer customer);

    boolean existsPersonWithEmail(String email);

    void deleteCustomerById(Integer customerId);

    boolean existsCustomerWithId(Integer customer_id);

    void updateCustomer(Customer customer);
}
