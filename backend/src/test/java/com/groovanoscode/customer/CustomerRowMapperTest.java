package com.groovanoscode.customer;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRowMapperTest {

    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();

        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.getInt("id")).thenReturn(1);
        Mockito.when(resultSet.getString("name")).thenReturn("Jamila");
        Mockito.when(resultSet.getString("email")).thenReturn("jamila@gmail.com");
        Mockito.when(resultSet.getInt("age")).thenReturn(19);

        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        Customer expected = new Customer(1, "Jamila", "jamila@gmail.com", 19);

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}