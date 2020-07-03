package de.fsteffen.sample.japogrpc.client.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fsteffen.sample.japogrpc.client.BookServiceReactorStub;
import de.fsteffen.sample.japogrpc.client.web.model.Book;
import de.fsteffen.sample.japogrpc.grpc.BookProto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

import static org.springframework.http.HttpStatus.OK;

@Component
public class BookHandler {

    private final BookMapper bookMapper;
    private final ObjectMapper objectMapper;
    private final BookServiceReactorStub bookServiceReactorStub;

    @Autowired
    public BookHandler(BookMapper bookMapper, ObjectMapper objectMapper, BookServiceReactorStub bookServiceReactorStub) {
        this.bookMapper = bookMapper;
        this.objectMapper = objectMapper;
        this.bookServiceReactorStub = bookServiceReactorStub;
    }

    @NonNull
    public Mono<ServerResponse> renderList(ServerRequest request) {
        request.attributes().put("books", bookServiceReactorStub.listBooks().map(bookMapper::mapFromGrpcModel));
        return ServerResponse.status(OK).render("list", request.attributes());
    }

    @NonNull
    public Mono<ServerResponse> renderAdd(ServerRequest request) {
        return ServerResponse.status(OK).render("edit", request.attributes());
    }

    @NonNull
    public Mono<ServerResponse> renderEdit(ServerRequest request) {
        final BookProto.BookId bookId = BookProto.BookId.newBuilder()
                .setId(Long.parseLong(request.pathVariable("id")))
                .build();

        request.attributes().put("book", bookServiceReactorStub.findBook(bookId).map(bookMapper::mapFromGrpcModel));
        return ServerResponse.status(OK).render("edit", request.attributes());
    }

    @NonNull
    public Mono<ServerResponse> save(ServerRequest request) {
        return request.formData()
                .map(MultiValueMap::toSingleValueMap)
                .map(v -> objectMapper.convertValue(v, Book.class))
                .map(bookMapper::mapToGrpcModel)
                .flatMap(bookServiceReactorStub::saveBook)
                .flatMap(ignored -> ServerResponse.status(OK).render("redirect:list", request.attributes()));
    }

}
