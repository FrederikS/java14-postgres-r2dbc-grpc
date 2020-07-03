package de.fsteffen.sample.japogrpc.domain;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

interface BookRepository extends ReactiveCrudRepository<Book, Long> {}