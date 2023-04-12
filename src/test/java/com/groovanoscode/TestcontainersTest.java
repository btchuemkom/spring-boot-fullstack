package com.groovanoscode;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.assertj.core.api.Assertions;



@Testcontainers
public class TestcontainersTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer =
        new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("groovanoscode-dao-unit-test")
                .withUsername("groovanoscode")
                .withPassword("password");


    @Test
    void canStartPostgresDB() {
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();
        Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
        //Assertions.assertThat(postgreSQLContainer.isHealthy()).isTrue();
    }
}
