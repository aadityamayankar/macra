package com.mayankar.auth.service;

import com.mayankar.auth.dto.UserDetailsGIAM;
import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.model.AuthnToken;
import com.mayankar.model.UserProfile;
import com.mayankar.util.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@Service
public class UserProfileService {
    @Autowired
    private ApiClient apiClient;

    @Autowired
    UserProfileRepository userProfileRepository;

    public Mono<UserDetailsGIAM> getUserDetailsGIAM(AuthnToken authnToken) {
        MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<>();
        requestHeaders.add("Authorization", "Bearer " + authnToken.getAccessToken());

        return apiClient.get("https://www.googleapis.com/oauth2/v2/userinfo", UserDetailsGIAM.class, requestHeaders);
    }

    public Mono<UserProfile> upsertUser(UserDetailsGIAM userDetailsGIAM) {
        return userProfileRepository.findByEmail(userDetailsGIAM.getEmail())
                .switchIfEmpty(Mono.just(new UserProfile()))
                .flatMap(userProfile -> {
                    if (userProfile.getId() != null) {
                        userProfile.setName(userDetailsGIAM.getName());
                        userProfile.setEmail(userDetailsGIAM.getEmail());
                    } else {
                        userProfile.setName(userDetailsGIAM.getName());
                        userProfile.setEmail(userDetailsGIAM.getEmail());
                    }
                    return userProfileRepository.save(userProfile);
                });
    }
}
