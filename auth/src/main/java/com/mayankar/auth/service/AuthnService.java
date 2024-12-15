package com.mayankar.auth.service;

import com.mayankar.auth.dto.UserLoginRequest;
import com.mayankar.auth.dto.UserRegistrationRequest;
import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import com.mayankar.dataaccess.repository.RoleProfileRepository;
import com.mayankar.dataaccess.repository.UserPasswordInfoRepository;
import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.dataaccess.repository.UserRoleAssignmentRepository;
import com.mayankar.dto.AuthCodeRequest;
import com.mayankar.enums.UserRole;
import com.mayankar.model.AuthnSession;
import com.mayankar.model.UserPasswordInfo;
import com.mayankar.model.UserProfile;
import com.mayankar.model.UserRoleAssignment;
import com.mayankar.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;

@Service
public class AuthnService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private DomainUtils domainUtils;

    @Autowired
    private UserPasswordInfoRepository userPasswordInfoRepository;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private UserRoleAssignmentRepository userRoleAssignmentRepository;

    @Autowired
    private RoleProfileRepository roleProfileRepository;

    @Autowired
    private ConfigProps configProps;

    private static final Logger logger = LoggerFactory.getLogger(AuthnService.class);
    @Autowired
    private AuthnSessionRepository authnSessionRepository;

    public Mono<UserProfile> registerUser(UserRegistrationRequest userRegistrationRequest) {
        return userProfileRepository.findByEmail(userRegistrationRequest.getEmail())
                .switchIfEmpty(Mono.just(new UserProfile()))
                .flatMap(userProfile -> {
                    if (userProfile.getId() != null) {
                        return Mono.error(new RuntimeException("User already exists"));
                    }
                    userProfile.setName(domainUtils.getUsernameFromEmail(userRegistrationRequest.getEmail()));
                    userProfile.setEmail(userRegistrationRequest.getEmail());
                    return userProfileRepository.save(userProfile);
                })
                .flatMap(userProfile -> {
                    String encodedPassword = passwordEncoderUtil.encodePassword(userRegistrationRequest.getPassword());
                    UserPasswordInfo userPasswordInfo = UserPasswordInfo.builder()
                            .userId(userProfile.getId())
                            .passwordHash(encodedPassword)
                            .build();
                    return userPasswordInfoRepository.save(userPasswordInfo)
                            .map(userPasswordInfo1 -> userProfile)
                            .flatMap(userProfile1 -> roleProfileRepository.getRoleProfileByValue(UserRole.USER.getValue())
                                    .flatMap(roleProfile -> {
                                        UserRoleAssignment userRoleAssignment = UserRoleAssignment.builder()
                                                .userId(userProfile1.getId())
                                                .roleId(roleProfile.getId())
                                                .build();
                                        return userRoleAssignmentRepository.save(userRoleAssignment);
                                    })
                                    .map(userRoleAssignment -> userProfile1));
                });

    }

    public Mono<UserProfile> authenticateUser(UserLoginRequest userLoginRequest) {
        return userProfileRepository.findByEmail(userLoginRequest.getEmail())
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap(userProfile -> userPasswordInfoRepository.findByUserId(userProfile.getId())
                        .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                        .flatMap(userPasswordInfo -> {
                            if (passwordEncoderUtil.matches(userLoginRequest.getPassword(), userPasswordInfo.getPasswordHash())) {
                                return Mono.just(userProfile);
                            } else {
                                return Mono.error(new RuntimeException("Invalid password"));
                            }
                        })
                );
    }

    public Mono<Boolean> logoutUser(AuthnSession authnSession) {
        logger.debug("Logging out user: {}", authnSession.getUserId());
        return authnSessionRepository.deleteSession(authnSession.getId())
                .flatMap(sessionDeleted -> {
                    if (sessionDeleted) {
                        return Mono.just(true);
                    } else {
                        return Mono.error(new RuntimeException("Error logging out user"));
                    }
                });
    }

    public Mono<ResponseEntity<Void>> redirectToAuthorize(UserProfile userProfile, ServerWebExchange exchange) {
        //@TODO: use state to prevent CSRF attacks
        AuthCodeRequest authorizeCodeRequest = AuthCodeRequest.builder()
                .responseType(Constants.CODE)
                .clientId(configProps.getSelfClientId())
                .redirectUri(configProps.getSelfRedirectUri())
                .scope(getScopes())
                .userId(CompositeID.parseId(userProfile.getId()))
                .build();
        URI selfAuthzUri = UrlConfig.getSelfAuthzUri(authorizeCodeRequest);
        return Mono.just(ResponseEntity.status(302).location(selfAuthzUri).build());
    }

    private String getScopes() {
        ArrayList<String> scopes = new ArrayList<>();
        scopes.add(Constants.OPENID);
        scopes.add(Constants.READ);
        scopes.add(Constants.WRITE);
        return String.join(" ", scopes);
    }
}
