package edu.wctc.refactored;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wctc.data.Book;
import edu.wctc.data.Borrower;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Controller {

    private List<Borrower> borrowerList;
    private List<Book> availableBookList;

    private LibraryWindow window;
    public Controller() {
        readDataFiles();
        // Put the list data into the window components for display
        window.displayAvailableBooks(availableBookList);
        window.displayBorrowers(borrowerList);




    }
    public void readDataFiles() {
        // Create empty lists
        availableBookList = new ArrayList<>();
        borrowerList = new ArrayList<>();

        // Create a JSON object mapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Create Java objects from the JSON data and stream into the lists
            Stream.of(mapper.readValue(new File("books.json"), Book[].class)).forEach(availableBookList::add);
            Stream.of(mapper.readValue(new File("borrowers.json"), Borrower[].class)).forEach(borrowerList::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void returnBook() {
        // Get selections from the JLists (window widgets)
        Borrower borrower = window.getSelectedBorrower();
        Book book = window.getSelectedBookOnLoan();

        // Ensure one of each is selected
        if (book == null || borrower == null) {
            JOptionPane.showMessageDialog(window, "You must select a borrower and a book to return.");
        } else {
            // Add the book to the data list and remove from the borrower
            availableBookList.add(book);
            borrower.returnBook(book);

            // Update the screen to show the book is checked out
            window.refreshBookDisplay(availableBookList);
        }
    }
    public void searchBooks() {
        // Get search string from the text box
        String searchTerm = window.getBookSearchTerm();

        // Filter available books to those matching the search string
        List<Book> matches = availableBookList.stream()
                .filter(b ->
                        b.getAuthor().toString().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                b.getTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        // Display matches
        window.displayAvailableBooks(matches);
    }
    public void searchBorrowers() {
        // Get search string from the text box
        String searchTerm = window.getBorrowerSearchTerm();

        // Filter borrowers to those matching the search string
        List<Borrower> matches = borrowerList.stream()
                .filter(b -> b.toString().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

        // Display matches
        window.displayBorrowers(matches);
    }

    public void checkoutBook() {
        // Get selections from the JLists (window widgets)
        Borrower borrower = window.getSelectedBorrower();
        Book book = window.getSelectedAvailableBook();

        // Ensure one of each is selected
        if (book == null || borrower == null) {
            JOptionPane.showMessageDialog(window, "You must select a borrower and a book to checkout.");
        } else {
            // Remove book from data list and add to selected borrower
            availableBookList.remove(book);
            borrower.checkoutBook(book);

            // Update the screen to show the book is checked out
            window.refreshBookDisplay(availableBookList);
        }
    }
    public void writeDataFiles() {
        // Create a JSON mapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Write the data lists as JSON to the files
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("books.json"), availableBookList);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("borrowers.json"), borrowerList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
