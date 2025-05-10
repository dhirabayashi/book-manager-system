package com.github.dhirabayashi.bookmanager.test

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@Configuration
@TestConfiguration
class TestcontainersConfig {

    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> {
        return PostgreSQLContainer("postgres:17.4").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
            withReuse(true)
            start()
        }
    }

    @Bean
    fun dataSource(postgresContainer: PostgreSQLContainer<*>): DataSource {
        return HikariDataSource(HikariConfig().apply {
            jdbcUrl = postgresContainer.jdbcUrl
            username = postgresContainer.username
            password = postgresContainer.password
            driverClassName = postgresContainer.driverClassName
        })
    }
}
