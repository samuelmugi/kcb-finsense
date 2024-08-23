package com.mugi.finsense.config.data;

import com.mugi.finsense.entity.Book;
import com.mugi.finsense.repos.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DataInitializerService {

    @Autowired
    private BookRepository bookRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void run()  {
        // Add two initial books to the database
        Book book1 = new Book();
        book1.setTitle("Kifo Kisimani");
        book1.setAuthor("Kithaka Mberia");
        book1.setYear(1925);
        bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("The River Between");
        book2.setAuthor("Ngugi wa Thingo");
        book2.setYear(1960);
        bookRepository.save(book2);

        System.out.println("Initial data loaded into the database.");
    }
}
