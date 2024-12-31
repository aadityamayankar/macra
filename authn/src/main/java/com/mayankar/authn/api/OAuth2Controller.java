package com.mayankar.authn.api;

import com.mayankar.authn.service.*;
import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import com.mayankar.util.ConfigProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("login/oauth2")
public class OAuth2Controller {

    @Autowired
    private OAuth2Service oauth2Service;
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

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);
    @Autowired
    private ConfigProps configProps;

    @GetMapping("/code/google")
    public Mono<Void> googleCallback(@RequestParam("code") String code,
                                        @RequestParam("scope") String scope,
                                        @RequestParam(value = "authuser", required = false) String authuser,
                                        @RequestParam(value = "prompt", required = false) String prompt,
                                        @RequestParam(value = "state", required = false) String state,
                                        ServerWebExchange exchange
    ) {
        logger.info("Received code from google: {}, with scope: {}", code, scope);
        return oauth2Service.exchangeCodeForTokenGIAM(code)
                .flatMap(authnToken -> userProfileService.getUserDetailsGIAM(authnToken)
                        .flatMap(userDetailsGIAM -> userProfileService.upsertUser(userDetailsGIAM))
                        .flatMap(userProfile -> userProfileWithDetailsService.getUserProfileWithDetails(userProfile))
                        .flatMap(user -> authnSessionService.createAuthnSession(user, authnToken))
                        .flatMap(authnSession -> authnSessionRepository.save(authnSession)))
                .flatMap(authnSession -> {
                    ResponseCookie sessionCookie = cookieService.createSessionCookie(authnSession);
                    exchange.getResponse().addCookie(sessionCookie);
                    exchange.getResponse().setStatusCode(HttpStatus.FOUND);
                    String redirectUrl = state != null ? state : configProps.getNextJSBaseUrl();
                    exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
                    return exchange.getResponse().setComplete();
                });
    }
    @GetMapping("/code/self")
    public Mono<Void> selfCallback(@RequestParam("code") String code,
                                   @RequestParam("scope") String scope,
                                   @RequestParam("user_id") String userId,
                                   @RequestParam(value = "authuser", required = false) String authuser,
                                   @RequestParam(value = "prompt", required = false) String prompt,
                                   ServerWebExchange exchange
    ) {
        logger.info("Received code from self: {}, with scope: {}", code, scope);
        return oauth2Service.exchangeCodeForTokenSelf(code)
                .flatMap(authnToken -> userProfileService.getUserProfile(userId)
                        .flatMap(userProfile -> userProfileWithDetailsService.getUserProfileWithDetails(userProfile))
                        .flatMap(user -> authnSessionService.createAuthnSession(user, authnToken))
                        .flatMap(authnSession -> authnSessionRepository.save(authnSession)))
                .flatMap(authnSession -> {
                    ResponseCookie sessionCookie = cookieService.createSessionCookie(authnSession);
                    exchange.getResponse().addCookie(sessionCookie);
                    return Mono.empty();
                });
    }
}
