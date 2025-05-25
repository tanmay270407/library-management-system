package com.library.gui;

import com.library.dao.BookDAO;
import com.library.model.Book;
import com.library.util.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Main GUI class for the Library Management System
 * Provides user interface for managing books
 */
public class LibraryGUI extends JFrame {
    private DefaultTableModel tableModel;
    private final BookDAO bookDAO;
    private JTable bookTable;
    private JTextField titleField;
    private JTextField authorField;
    private JTextField searchField;
    private JButton addButton;
    private JButton borrowButton;
    private JButton returnButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton refreshButton;

    public LibraryGUI() {
        this.bookDAO = new BookDAO();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        refreshTable();
    }

    /**
     * Initialize GUI components
     */
    private void initializeComponents() {
        // Frame setup
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        // Input fields
        titleField = new JTextField();
        authorField = new JTextField();
        searchField = new JTextField();

        titleField.setToolTipText("Enter book title");
        authorField.setToolTipText("Enter author's name");
        searchField.setToolTipText("Search by title or author");

        // Buttons
        addButton = new JButton("Add Book");
        borrowButton = new JButton("Borrow Book");
        returnButton = new JButton("Return Book");
        deleteButton = new JButton("Delete Book");
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        // Set mnemonics for keyboard shortcuts
        addButton.setMnemonic(KeyEvent.VK_A);
        borrowButton.setMnemonic(KeyEvent.VK_B);
        returnButton.setMnemonic(KeyEvent.VK_R);
        deleteButton.setMnemonic(KeyEvent.VK_D);
        searchButton.setMnemonic(KeyEvent.VK_S);

        // Set tooltips
        addButton.setToolTipText("Add new book to library (Alt+A)");
        borrowButton.setToolTipText("Mark selected book as borrowed (Alt+B)");
        returnButton.setToolTipText("Mark selected book as returned (Alt+R)");
        deleteButton.setToolTipText("Delete selected book (Alt+D)");
        searchButton.setToolTipText("Search books (Alt+S)");
        refreshButton.setToolTipText("Refresh book list");

        // Table setup
        String[] columnNames = {"ID", "Title", "Author", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(30);
        bookTable.getTableHeader().setReorderingAllowed(false);
        
        // Enable table sorting
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        // Set column widths
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        bookTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Actions
    }

    /**
     * Setup the layout of components
     */
    private void setupLayout() {
        // Top panel for adding books
        JPanel addBookPanel = createAddBookPanel();

        // Middle panel for search
        JPanel searchPanel = createSearchPanel();

        // Center panel for table
        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Book Collection"));

        // Bottom panel for action buttons
        JPanel actionPanel = createActionPanel();

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(addBookPanel, BorderLayout.NORTH);
        contentPanel.add(searchPanel, BorderLayout.CENTER);

        // Add all panels to frame
        add(contentPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    /**
     * Create the add book panel
     */
    private JPanel createAddBookPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Book"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(titleField, gbc);

        // Author
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(authorField, gbc);

        // Add button
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 2; gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 0;
        panel.add(addButton, gbc);

        return panel;
    }

    /**
     * Create the search panel
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Books"));
        
        panel.add(new JLabel("Search:"));
        searchField.setPreferredSize(new Dimension(200, 25));
        panel.add(searchField);
        panel.add(searchButton);
        panel.add(refreshButton);

        return panel;
    }

    /**
     * Create the action buttons panel
     */
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Book Actions"));
        
        panel.add(borrowButton);
        panel.add(returnButton);
        panel.add(deleteButton);

        return panel;
    }

    /**
     * Setup event listeners for all components
     */
    private void setupEventListeners() {
        // Add book button
        addButton.addActionListener(new AddBookListener());

        // Borrow book button
        borrowButton.addActionListener(new BorrowBookListener());

        // Return book button
        returnButton.addActionListener(new ReturnBookListener());

        // Delete book button
        deleteButton.addActionListener(new DeleteBookListener());

        // Search button
        searchButton.addActionListener(new SearchBookListener());

        // Refresh button
        refreshButton.addActionListener(e -> refreshTable());

        // Enter key listeners for text fields
        titleField.addActionListener(new AddBookListener());
        authorField.addActionListener(new AddBookListener());
        searchField.addActionListener(new SearchBookListener());

        // Table selection listener
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }

    /**
     * Update button states based on table selection
     */
    private void updateButtonStates() {
        int selectedRow = bookTable.getSelectedRow();
        boolean hasSelection = selectedRow != -1;
        
        borrowButton.setEnabled(hasSelection);
        returnButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);

        if (hasSelection) {
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            String status = tableModel.getValueAt(modelRow, 3).toString();
            borrowButton.setEnabled(status.equals("Available"));
            returnButton.setEnabled(status.equals("Not Available"));
        }
    }

    /**
     * Refresh the table with current data
     */
    private void refreshTable() {
        refreshTable(bookDAO.getAllBooks());
    }

    /**
     * Refresh table with specific book list
     */
    private void refreshTable(List<Book> books) {
        tableModel.setRowCount(0);
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.isAvailable() ? "Available" : "Not Available",
                "Actions"
            });
        }
        updateButtonStates();
    }

    // Event Listener Classes
    private class AddBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(LibraryGUI.this,
                    "Both Title and Author fields are required.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            Book newBook = new Book(0, title, author, true);
            if (bookDAO.addBook(newBook)) {
                titleField.setText("");
                authorField.setText("");
                refreshTable();
            }
        }
    }

    private class BorrowBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = bookTable.convertRowIndexToModel(selectedRow);
                int bookId = (int) tableModel.getValueAt(modelRow, 0);
                String status = tableModel.getValueAt(modelRow, 3).toString();
                
                if (status.equals("Available")) {
                    if (bookDAO.updateAvailability(bookId, false)) {
                        refreshTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(LibraryGUI.this,
                        "This book is already borrowed.",
                        "Cannot Borrow",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private class ReturnBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = bookTable.convertRowIndexToModel(selectedRow);
                int bookId = (int) tableModel.getValueAt(modelRow, 0);
                String status = tableModel.getValueAt(modelRow, 3).toString();
                
                if (status.equals("Not Available")) {
                    if (bookDAO.updateAvailability(bookId, true)) {
                        refreshTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(LibraryGUI.this,
                        "This book is already available.",
                        "Cannot Return",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    private class DeleteBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = bookTable.convertRowIndexToModel(selectedRow);
                int bookId = (int) tableModel.getValueAt(modelRow, 0);
                String title = tableModel.getValueAt(modelRow, 1).toString();
                
                int result = JOptionPane.showConfirmDialog(LibraryGUI.this,
                    "Are you sure you want to delete the book: " + title + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    if (bookDAO.deleteBook(bookId)) {
                        refreshTable();
                    }
                }
            }
        }
    }

    private class SearchBookListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                refreshTable();
            } else {
                List<Book> searchResults = bookDAO.searchBooks(searchTerm);
                refreshTable(searchResults);
            }
        }
    }
}