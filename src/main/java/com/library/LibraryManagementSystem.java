package com.library;

import com.library.gui.LibraryGUI;
import com.library.util.DatabaseConnection;

import javax.swing.*;

/**
 * Main application class for Library Management System
 * Entry point of the application
 */
public class LibraryManagementSystem {
    
    public static void main(String[] args) {
        // Set system look and feel
        setLookAndFeel();
        
        // Check database connection
        if (!checkDatabaseConnection()) {
            showDatabaseError();
            return;
        }
        
        // Initialize database if needed
        initializeDatabase();
        
        // Start the GUI application
        SwingUtilities.invokeLater(() -> {
            try {
                new LibraryGUI().setVisible(true);
            } catch (Exception e) {
                showError("Error starting application: " + e.getMessage());
            }
        });
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
    }

    private static boolean checkDatabaseConnection() {
        try {
            return DatabaseConnection.testConnection();
        } catch (Exception e) {
            return false;
        }
    }

    private static void initializeDatabase() {
        try {
            DatabaseConnection.initializeDatabase();
        } catch (Exception e) {
            showError("Failed to initialize database: " + e.getMessage());
        }
    }

    private static void showDatabaseError() {
        JOptionPane.showMessageDialog(null,
            "Could not connect to database. Please check your database settings.",
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }

    private static void showError(String message) {
        JOptionPane.showMessageDialog(null,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}