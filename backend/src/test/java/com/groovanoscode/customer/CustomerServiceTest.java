package com.groovanoscode.customer;

import com.groovanoscode.exception.DuplicateResourceException;
import com.groovanoscode.exception.RequestValidationException;
import com.groovanoscode.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;

    @Mock
    private CustomerDao customerDao;
    @Mock
    PasswordEncoder passwordEncoder;

    private final CustomerDTOMapper customerDTOMapper = new CustomerDTOMapper();


    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao , customerDTOMapper , passwordEncoder);

    }


    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        Mockito.verify(customerDao).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer)); // Optional because the return typ of selectCustomerById is an Optional

        CustomerDTO expected = customerDTOMapper.apply(customer);

        // When
        CustomerDTO actual = underTest.getCustomer(id);

        // Then
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        int id = 10;

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When

        // Then
        Assertions.assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with [%s] not found" .formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, "password" , 19, Gender.MALE
        );

        String passwordHash = "ç5554ml;f;lsd"; //just put some random number as hashcode
        Mockito.when(passwordEncoder.encode(request.password())).thenReturn(passwordHash);

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getId()).isNull(); // Id is not because it is generated by the database
        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        Assertions.assertThat(capturedCustomer.getPassword()).isEqualTo(passwordHash);
    }

    @Test
    void willThrowWhenEmailExistWhileAddingACustomer() {
        // Given
        String email = "alex@gmail.com";

        Mockito.when(customerDao.existsPersonWithEmail(email)).thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, "password" , 19, Gender.MALE
        );

        // When
        Assertions.assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then: Verify that the customerDao.insertCustomer() is never invoke or will never insert a customer
        Mockito.verify(customerDao, Mockito.never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomer() {
        // Given
        int id = 10;
        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomer(id);

        // Then
        Mockito.verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdNotExistWhileDeletingACustomer() {
        // Given
        int id = 10;
        Mockito.when(customerDao.existsCustomerWithId(id)).thenReturn(false);

        // When
        Assertions.assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));

        // Then
        Mockito.verify(customerDao, Mockito.never()).deleteCustomerById(id);
    }

    @Test
    void getCustomerById() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomerById(id);

        // Then
        Assertions.assertThat(actual.getId()).isEqualTo(customer.getId());
        Assertions.assertThat(actual.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(actual.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenIdNotFoundWhileGettingCustomerById() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        Assertions.assertThatThrownBy(() -> underTest.getCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found".formatted(id)
                );

        // Then
        //Mockito.verify(customerDao).selectCustomerById(id);
        Assertions.assertThat(customerDao.selectCustomerById(id)).isEqualTo(Optional.empty());
    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@groovanoscode.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest("Alexandro", newEmail, 23, Gender.FEMALE);
        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        Assertions.assertThat(capturedCustomer.getGender()).isEqualTo(updateRequest.gender());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@groovanoscode.com";
        String newName = "Alexandro";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(newName, null, null, null);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(newName);
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        Assertions.assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@groovanoscode.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null, Gender.MALE);

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        Assertions.assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        int newAge = 29;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, newAge, Gender.MALE);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(newAge);
        Assertions.assertThat(capturedCustomer.getGender()).isEqualTo(customer.getGender());
    }

    @Test
    void canUpdateOnlyCustomerGender() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        Gender newGender = Gender.FEMALE;

        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, null, null, newGender);

        // When
        underTest.updateCustomerById(id, updateRequest);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();

        Assertions.assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        Assertions.assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
        Assertions.assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        Assertions.assertThat(capturedCustomer.getGender()).isEqualTo(newGender);
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);
        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@groovanoscode.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(null, newEmail, null, null);

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        // When
        Assertions.assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        int id = 10;
        Customer customer = new Customer(id, "Alex", "alex@gmail.com", "password" , 19, Gender.MALE);

        Mockito.when(customerDao.selectCustomerById(id)).thenReturn(Optional.of(customer));

        String newEmail = "alexandro@groovanoscode.com";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender());

        // When
        Assertions.assertThatThrownBy(() -> underTest.updateCustomerById(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());
    }


}