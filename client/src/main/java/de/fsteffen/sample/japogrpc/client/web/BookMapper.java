package de.fsteffen.sample.japogrpc.client.web;

import com.google.protobuf.UInt64Value;
import de.fsteffen.sample.japogrpc.client.web.model.Book;
import de.fsteffen.sample.japogrpc.grpc.BookProto.BookReply;
import de.fsteffen.sample.japogrpc.grpc.BookProto.BookRequest;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper
public interface BookMapper {

    // needed until jdk14 record support for mapstruct is released
    default BookRequest mapToGrpcModel(Book book) {
        final BookRequest.Builder bookRequestBuilder = BookRequest.newBuilder();
        Optional.ofNullable(book.id()).map(UInt64Value::of).ifPresent(bookRequestBuilder::setId);
        Optional.ofNullable(book.title()).ifPresent(bookRequestBuilder::setTitle);
        return bookRequestBuilder.build();
    }

    default Book mapFromGrpcModel(BookReply bookReply) {
        return new Book(bookReply.getId(), bookReply.getTitle());
    }
}
