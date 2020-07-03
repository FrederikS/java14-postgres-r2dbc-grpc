package de.fsteffen.sample.japogrpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.DatabaseClient;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.BaseStream;
import java.util.stream.Stream;

@Configuration
@EnableR2dbcRepositories
class DbConfiguration {

    private final static Logger LOG = LoggerFactory.getLogger(DbConfiguration.class);

    private final Resource schema;

    DbConfiguration(@Value("classpath:/schema.sql") Resource schema) {
        this.schema = schema;
    }

    @Bean
    InitializingBean initializeDbSchema(DatabaseClient client) {
        return () -> getSchema()
                .flatMap(s -> client.execute(s).fetch().rowsUpdated())
                .subscribe(count -> System.out.println("Schema created, rows updated: " + count + "."), System.err::println);
    }

    private Mono<String> getSchema() throws IOException {
        // TODO why does this not work?
        // Paths.get(schema.getURI());

        return Flux
                .using(this::readSchema, Flux::fromStream, BaseStream::close)
                .reduce((line1, line2) -> line1 + "\n" + line2);
    }

    private Stream<String> readSchema() throws IOException {
        final BufferedReader schemaReader = new BufferedReader(new InputStreamReader(schema.getInputStream()));
        return schemaReader.lines().onClose(() -> {
            try {
                schemaReader.close();
            } catch (IOException e) {
                LOG.error("Error while trying to close schema reader.", e);
            }
        });
    }

}
