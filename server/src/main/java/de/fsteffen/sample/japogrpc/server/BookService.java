package de.fsteffen.sample.japogrpc.server;

import com.google.protobuf.Empty;
import de.fsteffen.sample.japogrpc.domain.Book;
import de.fsteffen.sample.japogrpc.domain.BookComponent;
import de.fsteffen.sample.japogrpc.grpc.BookProto;
import de.fsteffen.sample.japogrpc.grpc.BookServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

import static de.fsteffen.sample.japogrpc.grpc.BookProto.BookId;
import static de.fsteffen.sample.japogrpc.grpc.BookProto.BookReply;
import static de.fsteffen.sample.japogrpc.grpc.BookProto.BookRequest;

@Service
class BookService extends BookServiceGrpc.BookServiceImplBase {

    private final BookComponent bookComponent;

    @Autowired
    public BookService(BookComponent bookComponent) {
        this.bookComponent = bookComponent;
    }

    @Override
    public void saveBook(BookRequest request, StreamObserver<BookReply> responseObserver) {
        bookComponent.save(toBook(request))
                .map(BookService::toBookReply)
                .transform(registerObs(responseObserver))
                .subscribe();
    }

    @Override
    public void findBook(BookId request, StreamObserver<BookReply> responseObserver) {
        bookComponent.findById(request.getId())
                .map(BookService::toBookReply)
                .transform(registerObs(responseObserver))
                .subscribe();
    }

    @Override
    public void listBooks(Empty request, StreamObserver<BookProto.BookReply> responseObserver) {
        bookComponent.findAll()
                .map(BookService::toBookReply)
                .doOnNext(responseObserver::onNext)
                .doOnError(responseObserver::onError)
                .doOnComplete(responseObserver::onCompleted)
                .subscribe();
    }

    private static Book toBook(BookRequest request) {
        return Optional.of(request)
                .filter(BookRequest::hasId)
                .map(withId -> new Book(withId.getId().getValue(), withId.getTitle()))
                .orElse(Book.of(request.getTitle()));
    }

    private static BookReply toBookReply(Book b) {
        return BookReply.newBuilder()
                .setId(b.id())
                .setTitle(b.title())
                .build();
    }

    private static <T> Function<Mono<T>, Mono<T>> registerObs(StreamObserver<T> responseObserver) {
        return bookReplyMono -> bookReplyMono
                .doOnSuccess(responseObserver::onNext)
                .doOnError(responseObserver::onError)
                .doAfterTerminate(responseObserver::onCompleted);
    }

}
