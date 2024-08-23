/**
 * Author: Mugi
 * https://github.com/samuelmugi
 * User:mugimacharia
 * Date:23/08/2024
 * Time:18:16
 */

package com.mugi.finsense.controller;

import com.mugi.finsense.entity.Book;
import com.mugi.finsense.repos.BookRepository;
import com.mugi.finsense.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
public class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookController(bookService)).build();
        bookRepository.deleteAll(); // Clear the database before each test
    }

    @Test
    public void testCreateAndRetrieveBook() throws Exception {
        String bookJson = "{\"title\":\"Spring Boot Guide\", \"author\":\"John Doe\", \"year\":2024}";

      mockMvc.perform(post("/api/v1/books")
                .contentType("application/json")
                .content(bookJson))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title").value("Spring Boot Guide"))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(jsonPath("$.year").value(2024));

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].title").value("Spring Boot Guide"))
                .andExpect(jsonPath("$[0].author").value("John Doe"))
                .andExpect(jsonPath("$[0].year").value(2024));
    }

    @Test
    public void testUpdateBook() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Kotlin Programming");
        book.setAuthor("John Doe");
        book.setYear(1925);
        Book savedBook = bookRepository.save(book);

        String updatedBookJson = "{\"title\":\"Spring Boot Updated\", \"author\":\"John Doe\", \"year\":2025}";

        mockMvc.perform(put("/api/v1/books/" + savedBook.getId())
                        .contentType("application/json")
                        .content(updatedBookJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.title").value("Spring Boot Updated"))
                .andExpect(jsonPath("$.year").value(2025));
    }

    @Test
    public void testDeleteBook() throws Exception {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Kotlin Programming");
        book.setAuthor("John Doe");
        book.setYear(1925);
        Book savedBook = bookRepository.save(book);

        mockMvc.perform(delete("/api/v1/books/" + savedBook.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$").isEmpty());
    }
}
