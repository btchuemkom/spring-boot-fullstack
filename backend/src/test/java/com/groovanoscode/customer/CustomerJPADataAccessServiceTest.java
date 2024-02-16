package com.groovanoscode.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;

    private AutoCloseable autoCloseable;

    private static final Faker FAKER = new Faker();

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);//We need to initialize the Mock, because we are using Mock annotations
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        // Given --> Given is not necessary for this test, because the methode selectAllCustomer does not have arguments
        Page<Customer> page = mock(Page.class);
        List<Customer> customerList = List.of(new Customer());
        when(page.getContent()).thenReturn(customerList);
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
        // When
        List<Customer> expected = underTest.selectAllCustomers();
        //underTest.selectAllCustomers();

        // Then
        assertThat(expected).isEqualTo(customerList);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(customerRepository).findAll(pageableArgumentCaptor.capture());
        assertThat(pageableArgumentCaptor.getValue()).isEqualTo(Pageable.ofSize(1000));
        //Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.selectCustomerById(id);

        // Then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID(),
                "password" , 20,
                Gender.MALE);

        // When
        underTest.insertCustomer(customer);

        // Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // Given
        String email = FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID();

        // When
        underTest.existsPersonWithEmail(email);

        // Then
        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 1;

        // When
        underTest.deleteCustomerById(id);

        // Then
        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void existsCustomerWithId() {
        // Given
        int id = 1;

        // When
        underTest.existsCustomerWithId(id);

        // Then
        Mockito.verify(customerRepository).existsCustomerById(id);
    }

    @Test
    void updateCustomer() {
        // Given
        Customer customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "_" + UUID.randomUUID(),
                "password" , 20,
                Gender.MALE);

        // When
        underTest.updateCustomer(customer);

        // Then
        Mockito.verify(customerRepository).save(customer);
    }
}