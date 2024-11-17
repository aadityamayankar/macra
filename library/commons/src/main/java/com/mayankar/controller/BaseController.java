package com.mayankar.controller;

import com.mayankar.model.AuthnSession;
import org.springframework.web.server.ServerWebExchange;

public class BaseController {
    public static final String API = "/api";
    public static final String VER_V1 = "/v1";
    public static final String API_V1 = API + VER_V1;

    public AuthnSession extractAuthnSession(ServerWebExchange exchange) {
        return exchange.getAttribute("authnSession");
    }

    public AuthnSession validateAuthnSession(ServerWebExchange exchange) {
        AuthnSession authnSession = extractAuthnSession(exchange);
        if (authnSession == null) {
            throw new RuntimeException("Invalid session");
        }
        return authnSession;
    }
}
