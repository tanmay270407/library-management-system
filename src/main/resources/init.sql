-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isAvailable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_title ON books (title);
CREATE INDEX IF NOT EXISTS idx_author ON books (author);
CREATE INDEX IF NOT EXISTS idx_availability ON books (isAvailable);
