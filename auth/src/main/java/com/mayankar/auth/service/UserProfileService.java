package com.mayankar.auth.service;

import com.mayankar.auth.dto.UserDetailsGIAM;
import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.model.AuthnToken;
import com.mayankar.model.UserProfile;
import com.mayankar.util.ApiClient;
import com.mayankar.util.UrlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static com.mayankar.util.Constants.AUTHORIZATION;
import static com.mayankar.util.Constants.BEARER;

@Service
public class UserProfileService {
    @Autowired
    private ApiClient apiClient;

    @Autowired
    UserProfileRepository userProfileRepository;

    public Mono<UserDetailsGIAM> getUserDetailsGIAM(AuthnToken authnToken) {
        MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
        requestHeaders.add(AUTHORIZATION, BEARER + " " + authnToken.getAccessToken());

        return apiClient.get(UrlConfig.getGoogleUserinfoUrl(), UserDetailsGIAM.class, requestHeaders);
    }

    public Mono<UserProfile> upsertUser(UserDetailsGIAM userDetailsGIAM) {
        return userProfileRepository.findByEmail(userDetailsGIAM.getEmail())
                .switchIfEmpty(Mono.just(new UserProfile()))
                .flatMap(userProfile -> {
                    if (userProfile.getId() != null) {
                        userProfile.setName(userDetailsGIAM.getName());
                        userProfile.setEmail(userDetailsGIAM.getEmail());
                        userProfile.setModifiedAt(Instant.now());
                    } else {
                        userProfile.setName(userDetailsGIAM.getName());
                        userProfile.setEmail(userDetailsGIAM.getEmail());
                    }
                    return userProfileRepository.save(userProfile);
                });
    }
}
