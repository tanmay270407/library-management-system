package com.library.dao;

import com.library.model.Book;
import com.library.util.DatabaseConnection;
import javax.swing.JOptionPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book operations
 * Handles all database operations related to books
 */
public class BookDAO {
    
    /**
     * Check if a book already exists in the database
     * @param title Book title
     * @param author Book author
     * @return true if book exists, false otherwise
     */
    public boolean bookExists(String title, String author) {
        String sql = "SELECT COUNT(*) FROM books WHERE title = ? AND author = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, title);
            stmt.setString(2, author);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
            
        } catch (SQLException e) {
            showError("Error checking book existence: " + e.getMessage());
            return false;
        }
    }

    /**
     * Add a new book to the database
     * @param book Book object to be added
     * @return true if book was added successfully, false otherwise
     */
    public boolean addBook(Book book) {
        if (bookExists(book.getTitle(), book.getAuthor())) {
            JOptionPane.showMessageDialog(null, 
                "Book already exists in the library.", 
                "Duplicate Entry", 
                JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO books(title, author, isAvailable) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setBoolean(3, book.isAvailable());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Book added successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            showError("Error adding book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Retrieve all books from the database
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("isAvailable")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            showError("Error retrieving books: " + e.getMessage());
        }
        
        return books;
    }

    /**
     * Update book availability status
     * @param bookId ID of the book to update
     * @param available New availability status
     * @return true if update was successful, false otherwise
     */
    public boolean updateAvailability(int bookId, boolean available) {
        String sql = "UPDATE books SET isAvailable = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, available);
            stmt.setInt(2, bookId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                String message = available ? "Book returned successfully!" : "Book borrowed successfully!";
                JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            showError("Error updating book availability: " + e.getMessage());
        }
        return false;
    }

    /**
     * Search books by title or author
     * @param searchTerm Search term to look for
     * @return List of matching books
     */
    public List<Book> searchBooks(String searchTerm) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("isAvailable")
                );
                books.add(book);
            }
            
        } catch (SQLException e) {
            showError("Error searching books: " + e.getMessage());
        }
        
        return books;
    }

    /**
     * Delete a book from the database
     * @param bookId ID of the book to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Book deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            
        } catch (SQLException e) {
            showError("Error deleting book: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get a specific book by ID
     * @param bookId ID of the book
     * @return Book object or null if not found
     */
    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getBoolean("isAvailable")
                );
            }
            
        } catch (SQLException e) {
            showError("Error retrieving book: " + e.getMessage());
        }
        return null;
    }

    /**
     * Display error messages
     * @param message Error message to display
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}