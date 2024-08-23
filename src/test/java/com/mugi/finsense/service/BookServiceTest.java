package com.mugi.finsense.service;

import com.mugi.finsense.entity.Book;
import com.mugi.finsense.exception.ResourceNotFoundException;
import com.mugi.finsense.repos.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;



    @Test
    public void shouldCreateNewBook() {
        Book book = new Book();
        book.setTitle("Kifo Kisimani");
        book.setAuthor("Kithaka Mberia");
        book.setYear(1925);
        Book savedBook = new Book();
        savedBook.setTitle("Kifo Kisimani");
        savedBook.setAuthor("Kithaka Mberia");
        savedBook.setYear(1925);
        when(bookRepository.save(book)).thenReturn(savedBook);

        Book result = bookService.createBook(book);

        assertEquals(savedBook, result);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void shouldReturnAllBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Kotlin Programming");
        book1.setAuthor("John Doe");
        book1.setYear(1925);
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Spring Boot Essentials");
        book2.setAuthor("Jane Doe");
        book2.setYear(1925);
        List<Book> books = Arrays.asList(book1, book2);

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(books, result);
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    public void shouldReturnBookById() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Kotlin Programming");
        book.setAuthor("John Doe");
        book.setYear(1925);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(1L);

        assertEquals(book, result);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    public void shouldUpdateExistingBook() {
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setTitle("Kotlin Programming 1");
        existingBook.setAuthor("John Doe");
        existingBook.setYear(1925);
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Kotlin Programming 2");
        updatedBook.setAuthor("New Author");
        updatedBook.setYear(1925);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(ArgumentMatchers.any())).thenReturn(updatedBook);

        Book result = bookService.updateBook(1L, updatedBook);

        assertEquals(updatedBook, result);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(ArgumentMatchers.any());
    }

    @Test
    public void shouldThrowExceptionWhenBookNotFoundForUpdate() {
        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setTitle("Kotlin Programming 2");
        updatedBook.setAuthor("New Author");
        updatedBook.setYear(1925);
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            bookService.updateBook(1L, updatedBook);
        });
    }

    @Test
    public void shouldDeleteBook() {
        doNothing().when(bookRepository).deleteById(1L);

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }
}