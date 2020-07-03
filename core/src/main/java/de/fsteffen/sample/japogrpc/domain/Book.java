package de.fsteffen.sample.japogrpc.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("books")
public record Book(@Id Long id, String title) {

    public static Book of(String title) {
        return new Book(null, title);
    }

    Book withId(Long id) {
        return new Book(id, this.title);
    }

}
