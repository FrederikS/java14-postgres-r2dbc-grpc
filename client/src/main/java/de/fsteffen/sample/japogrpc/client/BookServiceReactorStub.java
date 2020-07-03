package de.fsteffen.sample.japogrpc.client;

import com.google.protobuf.Empty;
import de.fsteffen.sample.japogrpc.grpc.BookProto;
import de.fsteffen.sample.japogrpc.grpc.BookServiceGrpc.BookServiceStub;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

@Component
public class BookServiceReactorStub {

    private final BookServiceStub delegate;

    @Autowired
    BookServiceReactorStub(BookServiceStub delegate) {
        this.delegate = delegate;
    }

    public Mono<BookProto.BookReply> saveBook(BookProto.BookRequest request) {
        return Mono.create(monoSink -> delegate.saveBook(request, obs(monoSink)));
    }

    public Mono<BookProto.BookReply> findBook(BookProto.BookId request) {
        return Mono.create(monoSink -> delegate.findBook(request, obs(monoSink)));
    }

    public Flux<BookProto.BookReply> listBooks() {
        return Flux.create(fluxSink -> {
            delegate.listBooks(Empty.newBuilder().build(), new StreamObserver<>() {
                @Override
                public void onNext(BookProto.BookReply bookReply) {
                    fluxSink.next(bookReply);
                }

                @Override
                public void onError(Throwable throwable) {
                    fluxSink.error(throwable);
                }

                @Override
                public void onCompleted() {
                    fluxSink.complete();
                }
            });
        });
    }

    private static <T> StreamObserver<T> obs(MonoSink<T> monoSink) {
        return new StreamObserver<T>() {
            @Override
            public void onNext(T reply) {
                monoSink.success(reply);
            }

            @Override
            public void onError(Throwable throwable) {
                monoSink.error(throwable);
            }

            @Override
            public void onCompleted() {
                monoSink.success();
            }
        };
    }

}
