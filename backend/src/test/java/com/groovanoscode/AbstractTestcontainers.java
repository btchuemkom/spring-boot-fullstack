package com.groovanoscode;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

/**
 * abstract --> That mean, this class can be use only inside on this package (com.groovanoscode)
 * */
@Testcontainers
public abstract class AbstractTestcontainers {

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("groovanoscode-dao-unit-test")
                    .withUsername("groovanoscode")
                    .withPassword("password");
    @BeforeAll
    static void beforeAll() {
        //Before to run a test we must first execute the migration
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
    }

    /**
     * We need to connect our database 'groovanoscode-dao-unit-test' to our application
     * We can do this using DynamicPropertyRegistry and map the content of the
     * application.yml file to out database groovanoscode-dao-unit-test.
     * */
    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registry.add("spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registry.add("spring.datasource.password",
                postgreSQLContainer::getPassword
        );

    }

    private static DataSource getDataSource(){

        DataSourceBuilder builder = DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword());

        return builder.build();
    }

    protected static JdbcTemplate getJdbcTemplate(){
        return new JdbcTemplate(getDataSource());
    }

    protected static final Faker FAKER = new Faker();




}
