package de.fsteffen.sample.japogrpc.client.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Book(@JsonProperty("id") Long id, @JsonProperty("title") String title) { }
