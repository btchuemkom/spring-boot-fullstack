package com.groovanoscode;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.groovanoscode.customer.Customer;
import com.groovanoscode.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            /*Customer alex = new Customer("Alex", "alex@gmail.com", 21);
            Customer jamila = new Customer("Jamila", "jamila@gmail.com", 19);
            List<Customer> customers = List.of(alex, jamila);

            customerRepository.saveAll(customers);*/
            Faker faker = new Faker();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            var email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@groovanoscode.com";
            Random random = new Random();
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    email,
                    random.nextInt(16, 99)
            );

            customerRepository.save(customer);
        };
    }
}
