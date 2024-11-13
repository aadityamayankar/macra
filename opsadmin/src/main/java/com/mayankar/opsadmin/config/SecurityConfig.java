package com.mayankar.opsadmin.config;

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
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthenticationManager authenticationManager,
                                                         SessionAuthenticationConverter sessionAuthenticationConverter) {

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager);
        authenticationWebFilter.setServerAuthenticationConverter(sessionAuthenticationConverter);

        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/public/**").permitAll()
                        .anyExchange().access(this::hasOpsAdminRole)
                )
                .build();
    }

    private Mono<AuthorizationDecision> hasOpsAdminRole(Mono<Authentication> authentication, AuthorizationContext authorizationContext) {
        return authentication
                .map(Authentication::getDetails)
                .flatMap(authnSession -> {
                    if (authnSession instanceof AuthnSession) {
                        Integer role = ((AuthnSession) authnSession).getRole();
                        return Mono.just(UserRole.OPSADMIN.getValue() == role);
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
}
