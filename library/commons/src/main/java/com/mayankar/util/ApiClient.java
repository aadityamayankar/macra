package com.mayankar.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class ApiClient {
    @Autowired
    private WebClient webClient;

    public <T> Mono<T> get(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("GET request failed", e)));
    }

    public <T> Mono<T> get(String url, Class<T> responseType, MediaType contentType) {
        return webClient.get()
                .uri(url)
                .accept(contentType)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("GET request failed", e)));
    }

    public <T> Mono<T> get(String url, Class<T> responseType, MultiValueMap<String, String> requestHeaders) {
        return webClient.get()
                .uri(url)
                .headers(h -> h.addAll(requestHeaders))
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("GET request failed", e)));
    }

    public <T, R> Mono<T> post(String url, R requestBody, Class<T> responseType, MediaType contentType) {
        return webClient.post()
                .uri(url)
                .contentType(contentType)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("POST request failed", e)));
    }

    public <T, R> Mono<T> put(String url, R requestBody, Class<T> responseType, MediaType contentType) {
        return webClient.put()
                .uri(url)
                .contentType(contentType)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("PUT request failed", e)));
    }

    public <T> Mono<T> delete(String url, Class<T> responseType, MediaType contentType) {
        return webClient.delete()
                .uri(url)
                .accept(contentType)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("DELETE request failed", e)));
    }

    public <T, R> Mono<T> patch(String url, R requestBody, Class<T> responseType, MediaType contentType) {
        return webClient.patch()
                .uri(url)
                .contentType(contentType)
                .body(BodyInserters.fromValue(requestBody))
                .retrieve()
                .bodyToMono(responseType)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException("PATCH request failed", e)));
    }
}
