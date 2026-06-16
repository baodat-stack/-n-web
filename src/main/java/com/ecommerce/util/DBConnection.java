package com.ecommerce.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {

    private static HikariDataSource dataSource;

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.err.println("Sorry, unable to find db.properties");
                throw new RuntimeException("db.properties not found");
            }
            prop.load(input);

            // Configure HikariCP connection pool
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(prop.getProperty("db.url"));
            config.setUsername(prop.getProperty("db.username"));
            config.setPassword(prop.getProperty("db.password"));
            config.setDriverClassName(prop.getProperty("db.driver"));
            
            // Pool settings for performance
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000); // 30 seconds
            config.setConnectionTimeout(10000); // 10 seconds
            config.setMaxLifetime(1800000); // 30 minutes

            dataSource = new HikariDataSource(config);
            
            System.out.println("HikariCP Connection Pool Initialized Successfully.");
        } catch (Exception e) {
            System.err.println("Fatal Error during HikariCP Initialization:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }

    public static Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.err.println("Error getting connection from pool:");
            e.printStackTrace();
            return null;
        }
    }
}
