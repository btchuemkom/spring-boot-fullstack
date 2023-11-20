package com.groovanoscode.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao{

    //db
    private static List<Customer> customers;

    static{
        customers = new ArrayList<>();
        Customer alex = new Customer(1, "Alex", "alex@gmail.com", "password" , 21, Gender.MALE);
        customers.add(alex);
        Customer jamila = new Customer(2, "Jamila", "jamila@gmail.com", "password" , 19, Gender.FEMALE);
        customers.add(jamila);

    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        return customers.stream().filter(customer -> customer.getId().equals(customerId)).findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return customers.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        customers.stream().filter(customer -> customer.getId().equals(customerId))
                .findFirst().ifPresent(customers::remove);
    }

    @Override
    public boolean existsCustomerWithId(Integer customer_id) {
        return customers.stream().anyMatch(customer -> customer.getId().equals(customer_id));
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }

    @Override
    public Optional<Customer> selectUserByEmail(String email) {
        return customers.stream().filter(customer -> customer.getUsername().equals(email)).findFirst();
    }


}
