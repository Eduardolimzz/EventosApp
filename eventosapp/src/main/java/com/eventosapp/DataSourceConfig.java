package com.eventosapp;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DataSourceConfig {

    @Value("${MYSQLHOST:mysql.railway.internal}")
    private String host;

    @Value("${MYSQLPORT:3306}")
    private String port;

    @Value("${MYSQLDATABASE:railway}")
    private String database;

    @Value("${MYSQLUSER:root}")
    private String user;

    @Value("${MYSQLPASSWORD:vMWrSdCfnPkaByOnCRob5yducZ1onZZn}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        boolean isRailway = host != null && host.contains("railway");
        
        if (isRailway) {
            System.out.println("🚂 Configurando para RAILWAY...");
            return createRailwayDataSource();
        } else {
            System.out.println("🏠 Configurando para LOCAL...");
            return createLocalDataSource();
        }
    }

    private DataSource createRailwayDataSource() {
        HikariConfig config = new HikariConfig();
        
        String jdbcUrl = String.format(
            "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&maxReconnects=10",
            host, port, database
        );
        
        System.out.println("🔗 URL Railway: " + jdbcUrl);
        System.out.println("👤 User: " + user);
        System.out.println("🔑 Password: " + (password != null ? password.substring(0, 5) + "..." : "null"));
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(60000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(900000);
        config.setValidationTimeout(10000);
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(60000);
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        
        testConnection(dataSource);
        
        return dataSource;
    }

    private DataSource createLocalDataSource() {
        HikariConfig config = new HikariConfig();
        
        String jdbcUrl = "jdbc:mysql://localhost:3306/eventosapp?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true";
        
        System.out.println("🔗 URL Local: " + jdbcUrl);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername("root");
        config.setPassword("123456");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        config.setMaximumPoolSize(5);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        
        return new HikariDataSource(config);
    }

    private void testConnection(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("✅ Conexão estabelecida com sucesso!");
            
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("USE " + database);
                System.out.println("✅ Banco '" + database + "' selecionado com sucesso!");
                
                var resultSet = statement.executeQuery("SELECT DATABASE() as current_db");
                if (resultSet.next()) {
                    String currentDb = resultSet.getString("current_db");
                    System.out.println("✅ Banco atual: " + currentDb);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erro na conexão com o banco:");
            System.err.println("   Mensagem: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }
    }
}