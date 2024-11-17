package com.mayankar.dataaccess.repository;

import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public interface BaseRepository {
    <R> Flux<R> search(String query, MultiValueMap<String, ?> bindings, Class<R> returnType);

    <R> Flux<R> search(String query, Class<R> returnType);

    String WHERE = " WHERE ";

    String AND = " AND ";
}