package com.groovanoscode.customer;

import com.groovanoscode.AbstractTestcontainers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID(),
                20
        ); // We use "FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID()," because we want the email address to be unique. The name or the age must not be unique, we can have more than one user with the same name or age, it is not a problem
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomers();
        
        // Then
        Assertions.assertThat(actual).isNotEmpty();

    }

    @Test
    void selectCustomerById() {
        // Given
        //1. Add a customer to the database
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        ); // We use "FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID()," because we want the email address to be unique. The name or the age must not be unique, we can have more than one user with the same name or age, it is not a problem

        underTest.insertCustomer(customer);

        //2. get back the id of the saved customer. We can find the customer using his email
        int id = underTest.selectAllCustomers()
                .stream()
                .filter(savedCustomer -> savedCustomer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.selectCustomerById(id);

        // Then
        Assertions.assertThat(actual).isPresent().hasValueSatisfying(c -> {
            Assertions.assertThat(c.getId()).isEqualTo(id); // id or customer.getId()
            Assertions.assertThat(c.getName()).isEqualTo(customer.getName());
            Assertions.assertThat(c.getEmail()).isEqualTo(customer.getEmail()); // email or customer.getEmail()
            Assertions.assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        // Given
        int id = -1;

        // When
        var actual = underTest.selectCustomerById(id);

        // Then
        Assertions.assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        // Given

        // When

        // Then
    }

    @Test
    void existsPersonWithEmail() {
        // Given

        // When

        // Then
    }

    @Test
    void deleteCustomerById() {
        // Given

        // When

        // Then
    }

    @Test
    void existsCustomerWithId() {
        // Given

        // When

        // Then
    }

    @Test
    void updateCustomer() {
        // Given

        // When

        // Then
    }
}