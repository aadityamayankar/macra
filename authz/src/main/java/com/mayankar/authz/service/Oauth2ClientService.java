package com.mayankar.authz.service;

import com.mayankar.dto.AuthCodeRequest;
import com.mayankar.dto.BaseAuthRequest;
import com.mayankar.dataaccess.repository.Oauth2ClientRepository;
import com.mayankar.model.Oauth2Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class Oauth2ClientService {
    @Autowired
    Oauth2ClientRepository oauth2ClientRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public <T extends BaseAuthRequest> Mono<Oauth2Client> validateClient(T authRequest, Boolean checkClientSecret) {
        return oauth2ClientRepository.findByClientId(authRequest.getClientId())
                .filter(oauth2Client -> {
                    if (checkClientSecret && !passwordEncoder.matches(authRequest.getClientSecret(), oauth2Client.getClientSecret())) {
                        throw new IllegalArgumentException("Invalid client_secret");
                    }
                    if (!oauth2Client.getRedirectUris().contains(authRequest.getRedirectUri())) {
                        throw new IllegalArgumentException("Invalid redirect_uri");
                    }
                    return true;
                })
                .filter(oauth2Client -> {
                    if (authRequest instanceof AuthCodeRequest) {
                        String[] scopes = ((AuthCodeRequest)authRequest).getScope().split(" ");
                        for (String scope : scopes) {
                            if (!oauth2Client.getScopes().contains(scope)) {
                                return false;
                            }
                        }
                    }
                    return true;
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid client_id, redirect_uri or scope")));
    }
}