package de.fsteffen.sample.japogrpc.client.web;

import com.google.protobuf.UInt64Value;
import de.fsteffen.sample.japogrpc.client.web.model.Book;
import de.fsteffen.sample.japogrpc.grpc.BookProto.BookReply;
import de.fsteffen.sample.japogrpc.grpc.BookProto.BookRequest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Optional;

@Mapper
public interface BookMapper {

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    BookRequest mapToGrpcModel(Book book);

    Book mapFromGrpcModel(BookReply bookReply);

    default UInt64Value mapToUint(Long value) {
        return Optional.ofNullable(value)
                       .map(UInt64Value::of)
                       .orElse(null);
    }

}
