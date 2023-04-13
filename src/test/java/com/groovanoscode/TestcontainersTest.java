package com.groovanoscode;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

/**
 * Here we want to test the DAO Layer
 * This is an Unit-Test and for unit-test we should never user @SpringBootTest
 * otherwise the test will be really slow
 * */


public class TestcontainersTest extends AbstractTestcontainers {

    @Test
    void canStartPostgresDB() {
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();
        Assertions.assertThat(postgreSQLContainer.isCreated()).isTrue();
    }

}
