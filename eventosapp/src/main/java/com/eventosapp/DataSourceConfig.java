package com.eventosapp;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class DataSourceConfig {

    @Value("${MYSQLHOST:}")
    private String host;

    @Value("${MYSQLPORT:}")
    private String port;

    @Value("${MYSQLDATABASE:}")
    private String database;

    @Value("${MYSQLUSER:}")
    private String user;

    @Value("${MYSQLPASSWORD:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        if (host != null && !host.isEmpty()) {
            String jdbcUrl = String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database
            );
            System.out.println("🔗 Usando Railway: " + jdbcUrl);
            dataSource.setUrl(jdbcUrl);
            dataSource.setUsername(user);
            dataSource.setPassword(password);
        } else {
            System.out.println("🔗 Usando LOCAL: jdbc:mysql://localhost:3306/eventosapp");
            dataSource.setUrl("jdbc:mysql://localhost:3306/eventosapp?useSSL=false&serverTimezone=UTC");
            dataSource.setUsername("root");
            dataSource.setPassword("123456");
        }

        return dataSource;
    }
}
