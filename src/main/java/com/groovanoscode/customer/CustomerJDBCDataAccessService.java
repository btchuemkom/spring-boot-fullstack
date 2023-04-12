package com.groovanoscode.customer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCDataAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;

    public CustomerJDBCDataAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomers() {
        // Instead of "id, name, email, age" you can use *
        // SELECT * from customer
        var sql = """
                SELECT id, name, email, age
                FROM customer
                """;

        return jdbcTemplate.query(sql, customerRowMapper);

    }

    @Override
    public Optional<Customer> selectCustomerById(Integer customerId) {
        var sql = """
                SELECT id, name, email, age 
                FROM customer
                WHERE id=?
                """;


        return jdbcTemplate.query(sql, customerRowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer(name, email, age)
                VALUES (?, ?, ?)
                """;
        int result = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge());

        System.out.println("jdbcTemplate.update = " + result);

    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);

        return count != null && count > 0;
    }

    @Override
    public void deleteCustomerById(Integer customerId) {
        var sql = """
                DELETE
                FROM customer
                WHERE id = ?
                """;

        int result = jdbcTemplate.update(sql, customerId);
        System.out.println("deleteCustomerById result = " + result);
    }

    @Override
    public boolean existsCustomerWithId(Integer customer_id) {
        var sql = """
                SELECT count(id) 
                FROM customer
                WHERE id = ?
                """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, customer_id);

        return count != null && count > 0;
    }

    @Override
    public void updateCustomer(Customer customer) {
        if(customer.getName() != null){
            String sql = "UPDATE customer SET name = ? WHERE id = ?";

            int result = jdbcTemplate.update(sql, customer.getName(), customer.getId());

            System.out.println("update customer name result = " + result);
        }

        if(customer.getEmail() != null){
            String sql = "UPDATE customer SET email = ? WHERE id = ?";

            int result = jdbcTemplate.update(sql, customer.getEmail(), customer.getId());

            System.out.println("update customer email result = " + result);
        }

        if(customer.getAge() != null){
            String sql = "UPDATE customer SET age = ? WHERE id = ?";

            int result = jdbcTemplate.update(sql, customer.getAge(), customer.getId());

            System.out.println("update customer age result = " + result);
        }
    }
}
