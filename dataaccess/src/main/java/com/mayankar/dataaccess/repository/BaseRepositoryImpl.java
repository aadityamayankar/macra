package com.mayankar.dataaccess.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

@Component
public class BaseRepositoryImpl implements BaseRepository {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public <R> Flux<R> search(String query, MultiValueMap<String, ?> bindings, Class<R> returnType) {
        DatabaseClient.GenericExecuteSpec executeSpec = databaseClient.sql(query);
        for (String key : bindings.keySet()) {
            executeSpec = executeSpec.bind(key, bindings.getFirst(key));
        }
        return executeSpec.map((row, metaData) -> r2dbcEntityTemplate.getConverter().read(returnType, row, metaData)).all();
    }

    @Override
    public <R> Flux<R> search(String query, Class<R> returnType) {
        return databaseClient.sql(query).map((row, metaData) -> r2dbcEntityTemplate.getConverter().read(returnType, row, metaData)).all();
    }
}