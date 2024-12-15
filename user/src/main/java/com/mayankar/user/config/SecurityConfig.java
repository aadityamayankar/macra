package com.mayankar.user.config;

import com.mayankar.enums.UserRole;
import com.mayankar.model.AuthnSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            "/public/**",
            "/api/v1/webhooks/**",
            "/api/v1/cities/**",
            "/api/v1/events/**",
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager,
                                                         SessionAuthenticationConverter sessionAuthenticationConverter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(sessionAuthenticationConverter);

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        //@TODO: remove the webhooks path and look into doing it properly
                        .pathMatchers(AUTH_WHITELIST).permitAll()
                        .anyExchange().access(this::hasUserRole)
                )
                .exceptionHandling(exceptionHandlingSpec ->
                        exceptionHandlingSpec.authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .build();
    }

    private Mono<AuthorizationDecision> hasUserRole(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication
                .map(Authentication::getDetails)
                .flatMap(authnSession -> {
                    if (authnSession instanceof AuthnSession) {
                        Integer role = ((AuthnSession) authnSession).getRole();
                        return Mono.just(UserRole.USER.getValue() == role);
                    }
                    return Mono.just(false);
                })
                .flatMap(hasRole -> {
                    if (hasRole) {
                        return Mono.just(new AuthorizationDecision(true));
                    }
                    return Mono.just(new AuthorizationDecision(false));
                });
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedOrigin("http://localhost:3000"); //@TODO: add this as a config property
        corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }

    @Component
    private static class CustomAuthenticationEntryPoint extends HttpStatusServerEntryPoint {
        public CustomAuthenticationEntryPoint() {
            super(UNAUTHORIZED);
        }
    }
}