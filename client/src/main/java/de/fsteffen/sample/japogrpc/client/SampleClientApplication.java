package de.fsteffen.sample.japogrpc.client;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import de.fsteffen.sample.japogrpc.client.web.BookHandler;
import de.fsteffen.sample.japogrpc.grpc.BookServiceGrpc;
import de.fsteffen.sample.japogrpc.grpc.BookServiceGrpc.BookServiceStub;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.ServerResponse.permanentRedirect;

@SpringBootApplication
public class SampleClientApplication {

    private final BookHandler bookHandler;

    @Autowired
    public SampleClientApplication(BookHandler bookHandler) {
        this.bookHandler = bookHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(SampleClientApplication.class, args);
    }

    @Configuration
    static class GrpcConfig {
        @Bean
        BookServiceStub bookServiceStub() {
            return BookServiceGrpc.newStub(ManagedChannelBuilder
                    .forAddress("localhost", 8888)
                    .usePlaintext() // no-ssl
                    .build());
        }
    }

    @Bean
    public RouterFunction<ServerResponse> router() {
        return RouterFunctions.route()
                .GET("/", request -> permanentRedirect(URI.create("/list")).build())
                .GET("/list", bookHandler::renderList)
                .GET("/add", bookHandler::renderAdd)
                .GET("/edit/{id}", bookHandler::renderEdit)
                .POST("/save", accept(APPLICATION_FORM_URLENCODED), bookHandler::save)
                .build();
    }

    @Bean
    WebFilter addLayoutAttributeFilter(Mustache.Compiler compiler) {
        return (serverWebExchange, webFilterChain) -> {
            serverWebExchange.getAttributes().put("layout", new Layout(compiler));
            return webFilterChain.filter(serverWebExchange);
        };
    }

    static class Layout implements Mustache.Lambda {

        String body;

        private final Mustache.Compiler compiler;

        public Layout(Mustache.Compiler compiler) {
            this.compiler = compiler;
        }

        @Override
        public void execute(Template.Fragment frag, Writer out) throws IOException {
            body = frag.execute();
            compiler.compile("{{>layout}}").execute(frag.context(), out);
        }

    }

}
