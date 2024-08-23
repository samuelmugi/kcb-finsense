### Low-Level Design for Spring Boot Book Management Application

This low-level design (LLD) will provide detailed implementation guidelines for the components mentioned in the high-level design. It includes class definitions, method signatures, relationships, and specific configurations.

---

### 1. **Entity Layer**

**Class: `Book`**

- **Package:** `com.mugi.finsense.entity`
- **Annotations:**
    - `@Entity`: Marks this class as a JPA entity.
    - `@Table(name = "books")`: Specifies the table name in the database.

```java
package com.mugi.finsense.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Author is mandatory")
    private String author;

    private int year;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
```

### 2. **Repository Layer**

**Interface: `BookRepository`**

- **Package:** `com.mugi.finsense.repository`
- **Annotations:** None (Spring Data JPA will automatically detect this repository).

```java
package com.mugi.finsense.repository;

import com.mugi.finsense.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

### 3. **Service Layer**

**Class: `BookService`**

- **Package:** `com.mugi.finsense.service`
- **Annotations:**
    - `@Service`: Marks this class as a Spring service component.

```java
package com.mugi.finsense.service;

import com.mugi.finsense.entity.Book;
import com.mugi.finsense.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book createBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(bookDetails.getTitle());
            book.setAuthor(bookDetails.getAuthor());
            book.setYear(bookDetails.getYear());
            return bookRepository.save(book);
        }).orElseThrow(() -> new ResourceNotFoundException("Book not found with id " + id));
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
```

**Custom Exception: `ResourceNotFoundException`**

- **Package:** `com.mugi.finsense.exception`

```java
package com.mugi.finsense.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### 4. **Controller Layer**

**Class: `BookController`**

- **Package:** `com.mugi.finsense.controller`
- **Annotations:**
    - `@RestController`: Marks this class as a REST controller.
    - `@RequestMapping("/api/v1")`: Base URI for all endpoints.

```java
package com.mugi.finsense.controller;

import com.mugi.finsense.entity.Book;
import com.mugi.finsense.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/books")
    public Book createBook(@Valid @RequestBody Book book) {
        return bookService.createBook(book);
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody Book bookDetails) {
        Book updatedBook = bookService.updateBook(id, bookDetails);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 5. **Security Configuration**

**Class: `SecurityConfig`**

- **Package:** `com.mugi.finsense.config`
- **Annotations:**
    - `@Configuration`: Marks this class as a Spring configuration component.
    - `@EnableWebSecurity`: Enables Spring Security for the application.

```java
package com.mugi.finsense.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll() // Allow access to H2 console without authentication
                .anyRequest().authenticated()  // All other requests require authentication
                .and()
            .httpBasic()  // Use basic authentication
            .and()
            .csrf().disable() // Disable CSRF for the H2 console
            .headers().frameOptions().disable(); // Disable X-Frame-Options for H2 console
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        UserDetails user = User
            .withUsername("user")
            .password(passwordEncoder().encode("password"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 6. **Database Configuration**

**File: `application.yml`**

- **Location:** `src/main/resources/application.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.H2Dialect
```

### 7. **Validation Configuration**

**Entity Validation:**

- Use JSR-303/JSR-380 annotations such as `@NotBlank` in the `Book` entity to ensure that `title` and `author` fields are not empty.

**Controller Level Validation:**

- Annotate the `BookController` class methods with `@Validated` and method parameters with `@Valid` to enforce validation rules at the API level.

### 8. **Testing**

**Unit Testing:**

- **Tool:** Use JUnit and Mockito.
- **Tests:**
    - **Repository Layer:** Verify CRUD operations.
    - **Service Layer:** Test business logic and exception handling.
    - **Controller Layer:** Test API endpoints with MockMvc.

**Integration Testing:**

- Test the end-to-end flow, including the persistence of data in the H2 database and API responses.

---
