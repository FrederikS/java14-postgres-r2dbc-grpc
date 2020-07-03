package de.fsteffen.sample.japogrpc.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class BookComponent {

    private final BookRepository bookRepository;

    @Autowired
    public BookComponent(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Book> findById(long id) {
        return bookRepository.findById(id);
    }

    public Mono<Book> save(Book book) {
        return bookRepository.save(book);
    }

    public Flux<Book> findAll() {
        return bookRepository.findAll();
    }

}
