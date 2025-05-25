package com.library.util;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Database connection utility class
 * Manages database connections and initialization
 */
public class DatabaseConnection {
    private static final Properties props = new Properties();
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    private static boolean isInitialized = false;
    
    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                showError("Could not find database.properties");
                return;
            }
            
            props.load(input);
            DB_URL = props.getProperty("db.url");
            
            // Try environment variables first, then properties file
            DB_USER = System.getenv("DB_USER");
            if (DB_USER == null) {
                DB_USER = props.getProperty("db.user", "root");
            }
            
            DB_PASSWORD = System.getenv("DB_PASSWORD");
            if (DB_PASSWORD == null) {
                DB_PASSWORD = props.getProperty("db.password", "");
            }

            // Load the appropriate JDBC driver
            if (DB_URL.contains("mysql")) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if (DB_URL.contains("sqlite")) {
                Class.forName("org.sqlite.JDBC");
            }
            
            isInitialized = true;
        } catch (IOException e) {
            showError("Error loading database properties: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            showError("Database driver not found: " + e.getMessage());
        }
    }

    /**
     * Get database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (!isInitialized) {
            throw new SQLException("Database configuration not loaded");
        }
        
        if (DB_URL.contains("sqlite")) {
            return DriverManager.getConnection(DB_URL);
        } else {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
    }

    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            showError("Connection test failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize database schema
     */
    public static void initializeDatabase() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("init.sql")) {
            
            if (input == null) {
                showError("Could not find init.sql");
                return;
            }
            
            String sql = new String(input.readAllBytes());
            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {
                
                for (String statement : sql.split(";")) {
                    if (!statement.trim().isEmpty()) {
                        stmt.execute(statement);
                    }
                }
                System.out.println("Database initialized successfully!");
            }
            
        } catch (IOException e) {
            showError("Error reading init.sql: " + e.getMessage());
        } catch (SQLException e) {
            showError("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Display error message
     */
    private static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Close database connection safely
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                showError("Error closing connection: " + e.getMessage());
            }
        }
    }
}