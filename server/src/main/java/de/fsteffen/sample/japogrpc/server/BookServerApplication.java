package de.fsteffen.sample.japogrpc.server;

import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("de.fsteffen.sample.japogrpc")
public class BookServerApplication implements ApplicationRunner {

    private final BookService bookService;

    @Autowired
    public BookServerApplication(BookService bookService) {
        this.bookService = bookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookServerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ServerBuilder.forPort(8888)
                .addService(bookService)
                .build()
                .start()
                .awaitTermination();
    }
}
