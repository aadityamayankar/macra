package com.mayankar.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Getter
public class ConfigProps {
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    public String GIAMClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    public String GIAMClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    public String GIAMRedirectUri;
    @Value("${ibento.authn.session.timeout}")
    public Long authnSessionDuration;
    @Value("${ibento.authz.code.timeout}")
    public Long authzCodeDuration;
    @Value("${ibento.authz.baseurl}")
    public String authzBaseUrl;
    @Value("${ibento.authz.code.secret}")
    public String authzCodeSecret;
    @Value("${ibento.authz.refresh.timeout}")
    public Long authzRefreshDuration;
    @Value("${spring.security.oauth2.client.registration.self.client-id}")
    public String selfClientId;
    @Value("${spring.security.oauth2.client.registration.self.client-secret}")
    public String selfClientSecret;
    @Value("${spring.security.oauth2.client.registration.self.redirect-uri}")
    public String selfRedirectUri;
    @Value("${spring.rabbitmq.host}")
    public String rabbitMQHost;
    @Value("${spring.rabbitmq.port}")
    public Integer rabbitMQPort;
    @Value("${spring.rabbitmq.username}")
    public String rabbitMQUsername;
    @Value("${spring.rabbitmq.password}")
    public String rabbitMQPassword;
    @Value("${ibento.nextjs.baseurl}")
    public String nextJSBaseUrl;
}
