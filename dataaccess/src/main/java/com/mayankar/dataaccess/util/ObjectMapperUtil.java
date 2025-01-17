package com.mayankar.dataaccess.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperUtil {

    private final ObjectMapper mapper = createObjectMapper();

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
//        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public <T> T objectMapper(Object obj, Class<T> contentClass){
        return mapper.convertValue(obj, contentClass);
    }

}
