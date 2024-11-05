package com.mayankar.auth.api;

import com.mayankar.auth.dto.UserLoginRequest;
import com.mayankar.auth.dto.UserRegistrationRequest;
import com.mayankar.auth.service.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthnController {
    @Autowired
    private AuthnService authnService;

    private static final Logger logger = LoggerFactory.getLogger(AuthnController.class);

    @PostMapping("/register")
    public Mono<ResponseEntity<Void>> registerUser(@Valid @RequestBody UserRegistrationRequest userRegistrationRequest, ServerWebExchange exchange) {
        return authnService.registerUser(userRegistrationRequest)
                .flatMap(userProfile ->  authnService.redirectToAuthorize(userProfile, exchange))
                .doOnSuccess(s -> logger.info("User {} registered successfully", userRegistrationRequest.getEmail()))
                .onErrorResume(e -> {
                    logger.error("Error occurred while registering user: ", e);
                    return Mono.error(e);
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<Void>> loginUser(@Valid @RequestBody UserLoginRequest userLoginRequest, ServerWebExchange exchange) {
        return authnService.authenticateUser(userLoginRequest)
                .flatMap(userProfile -> authnService.redirectToAuthorize(userProfile, exchange))
                .doOnSuccess(s -> logger.info("User {} authenticated successfully", userLoginRequest.getEmail()))
                .onErrorResume(e -> {
                    logger.error("Error occurred while authenticating user: ", e);
                    return Mono.error(e);
                });
    }
}
