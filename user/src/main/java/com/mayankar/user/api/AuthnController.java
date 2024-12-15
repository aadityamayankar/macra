package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import com.mayankar.dto.SessionResponseDto;
import com.mayankar.model.AuthnSession;
import com.mayankar.user.service.AuthnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/auth")
public class AuthnController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(AuthnController.class);

    @Autowired
    AuthnService authnService;

    @GetMapping("/session")
    public Mono<SessionResponseDto> getSessionStatus(ServerWebExchange exchange) {
        AuthnSession authnSession = validateAuthnSession(exchange);
        return authnService.getSessionStatus(authnSession)
                .doOnSuccess(s -> logger.info("Session status retrieved successfully"))
                .onErrorResume(e -> {
                    logger.error("Error occurred while retrieving session status: ", e);
                    return Mono.error(e);
                });
    }
}
