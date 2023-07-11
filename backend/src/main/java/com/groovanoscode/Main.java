package com.groovanoscode;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.groovanoscode.customer.Customer;
import com.groovanoscode.customer.CustomerRepository;
import com.groovanoscode.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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
            int age = random.nextInt(16, 99);

            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;

            Customer customer = new Customer(
                    firstName + " " + lastName,
                    email,
                    random.nextInt(16, 99),
                    gender);

            customerRepository.save(customer);
        };
    }
}
