package com.eventosapp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EventosappApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventosappApplication.class, args);
    }

    @Bean
    CommandLineRunner createUser(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("CREATE USER 'appuser'@'%' IDENTIFIED BY 'senha123'");
                jdbcTemplate.execute("GRANT ALL PRIVILEGES ON railway.* TO 'appuser'@'%'");
                jdbcTemplate.execute("FLUSH PRIVILEGES");
                System.out.println("✅ Usuário 'appuser' criado com sucesso!");
            } catch (Exception e) {
                System.out.println("⚠️ Erro ao criar usuário (pode já existir): " + e.getMessage());
            }
        };
    }
}

