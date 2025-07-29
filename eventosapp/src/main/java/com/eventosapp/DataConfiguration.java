package com.eventosapp;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class DataConfiguration {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        String dbUrl = System.getenv("MYSQL_URL"); 
        String dbUsername = System.getenv("MYSQL_USERNAME"); 
        String dbPassword = System.getenv("MYSQL_PASSWORD");

        if (dbUrl == null || dbUrl.isEmpty()) {
            System.out.println("Variáveis de ambiente do banco de dados não encontradas. Usando configurações locais.");
            dataSource.setUrl("jdbc:mysql://localhost:3306/eventosapp?useTimezone=true&serverTimezone=UTC&useSSL=false");
            dataSource.setUsername("root");
            dataSource.setPassword("126357");
        } else {
            System.out.println("Variáveis de ambiente do banco de dados encontradas. Usando configurações do Railway.");
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUsername);
            dataSource.setPassword(dbPassword);
        }

        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true); 
        adapter.setPrepareConnection(true);
        return adapter;
    }
}