package com.mayankar.authn.service;

import com.mayankar.authn.dto.UserDetailsGIAM;
import com.mayankar.dataaccess.repository.RoleProfileRepository;
import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.dataaccess.repository.UserRoleAssignmentRepository;
import com.mayankar.enums.UserRole;
import com.mayankar.model.AuthnToken;
import com.mayankar.model.UserProfile;
import com.mayankar.model.UserRoleAssignment;
import com.mayankar.util.ApiClient;
import com.mayankar.util.CompositeID;
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
    @Autowired
    private UserRoleAssignmentRepository userRoleAssignmentRepository;
    @Autowired
    private RoleProfileRepository roleProfileRepository;

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
                })
                .flatMap(userProfile -> userRoleAssignmentRepository.findByUserId(userProfile.getId())
                        .switchIfEmpty(Mono.just(new UserRoleAssignment()))
                        .flatMap(userRoleAssignment -> {
                            if (userRoleAssignment.getId() == null) {
                                return roleProfileRepository.getRoleProfileByValue(UserRole.USER.getValue())
                                        .flatMap(roleProfile -> {
                                            userRoleAssignment.setUserId(userProfile.getId());
                                            userRoleAssignment.setRoleId(roleProfile.getId());
                                            return userRoleAssignmentRepository.save(userRoleAssignment);
                                        });
                            }
                            return Mono.just(userRoleAssignment);
                        })
                        .then(Mono.just(userProfile)));
    }

    public Mono<UserProfile> getUserProfile(String userId) {
        return userProfileRepository.findById(CompositeID.parseIdString(userId));
    }
}
