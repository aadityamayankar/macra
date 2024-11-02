package com.mayankar.auth.api;

import com.mayankar.auth.service.*;
import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("login/oauth2")
public class OAuthController {

    @Autowired
    private OAuthService OAuthService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private AuthnSessionService authnSessionService;
    @Autowired
    private AuthnSessionRepository authnSessionRepository;
    @Autowired
    private CookieService cookieService;
    @Autowired
    private UserProfileWithDetailsService userProfileWithDetailsService;

    @GetMapping("/code/google")
    public Mono<Void> googleCallback(@RequestParam("code") String code,
                                        @RequestParam("scope") String scope,
                                        @RequestParam("authuser") String authuser,
                                        @RequestParam("prompt") String prompt,
                                        ServerWebExchange exchange
    ) {
        return OAuthService.exchangeCodeForTokenGIAM(code)
                .flatMap(authnToken -> {
                    return userProfileService.getUserDetailsGIAM(authnToken)
                            .flatMap(userDetailsGIAM -> userProfileService.upsertUser(userDetailsGIAM))
                            .flatMap(userProfile -> userProfileWithDetailsService.getUserProfileWithDetails(userProfile))
                            .flatMap(user -> {
                                return authnSessionService.createAuthnSession(user, authnToken);
                            })
                            .flatMap(authnSession -> {
                                return authnSessionRepository.save(authnSession);
                            });
                })
                .flatMap(authnSession -> {
                    ResponseCookie sessionCookie = cookieService.createSessionCookie(authnSession);
                    exchange.getResponse().addCookie(sessionCookie);
                    return Mono.empty();
                });
    }

    @GetMapping("/test")
    public Mono<String> test(ServerWebExchange exchange) {
        return Mono.just("Test");
    }
}
