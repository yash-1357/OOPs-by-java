package com.exam.library_management;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {

    private List<Book> bookList = new ArrayList<>();

    // (b) Add a new book
    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        bookList.add(book);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Book added successfully with ID: " + book.getId());
    }

    // (c) Get all books
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookList);
    }

    // (c) Get book by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Integer id) {
        for (Book book : bookList) {
            if (book.getId().equals(id)) {
                return ResponseEntity.ok(book);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Book not found with ID: " + id);
    }

    // (d) Update book details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Integer id, @RequestBody Book updatedBook) {
        for (Book book : bookList) {
            if (book.getId().equals(id)) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                book.setCategory(updatedBook.getCategory());
                book.setAvailableCopies(updatedBook.getAvailableCopies());
                return ResponseEntity.ok(book);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Book not found with ID: " + id);
    }

    // (e) Delete book by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Integer id) {
        for (Book book : bookList) {
            if (book.getId().equals(id)) {
                bookList.remove(book);
                return ResponseEntity.ok("Book deleted successfully with ID: " + id);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Book not found with ID: " + id);
    }
}