# Library Management System

A simple desktop application for managing a library's book collection, built with Java Swing and MySQL/SQLite.

## Features

- Add new books to the library
- Search books by title or author
- Borrow and return books
- Delete books from the system
- Real-time availability status
- Clean and intuitive user interface

## Prerequisites

- Java 17 or higher
- Maven
- MySQL Server (or SQLite for simpler setup)

## Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd book-management-system
```

2. Configure the database:
   - Edit `src/main/resources/database.properties` with your database credentials
   - For MySQL: Run the initialization script:
     ```bash
     mysql -u root -p < src/main/resources/init.sql
     ```
   - For SQLite: Just uncomment the SQLite configuration in `database.properties`

3. Build the project:
```bash
mvn clean package
```

4. Run the application:
```bash
java -jar target/book-management-system-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Usage

1. Adding Books:
   - Enter the book title and author
   - Click "Add Book" or press Alt+A

2. Searching:
   - Type in the search box
   - Click "Search" or press Enter
   - Click "Refresh" to show all books

3. Managing Books:
   - Select a book from the list
   - Use "Borrow Book" (Alt+B) or "Return Book" (Alt+R)
   - Click "Delete Book" (Alt+D) to remove a book

## Development

The project uses Maven for dependency management and building:

- `mvn compile` - Compile the source code
- `mvn test` - Run tests
- `mvn package` - Create JAR file
- `mvn clean` - Clean build files

## Database Schema

The application uses a simple database schema with a single table:

```sql
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isAvailable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## Contributing

Feel free to fork the project and submit pull requests!
