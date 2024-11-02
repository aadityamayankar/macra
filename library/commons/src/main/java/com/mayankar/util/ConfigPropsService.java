package com.mayankar.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ConfigPropsService {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    public String GIAMClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    public String GIAMClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    public String GIAMRedirectUri;
    @Value("${ibento.authn.session.timeout}")
    public Long authnSessionDuration;
}
