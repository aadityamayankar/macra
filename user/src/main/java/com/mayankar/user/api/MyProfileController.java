package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import com.mayankar.dto.UserProfileDto;
import com.mayankar.model.AuthnSession;
import com.mayankar.user.service.MyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/my-profile")
public class MyProfileController extends BaseController {
    private static Logger logger = LoggerFactory.getLogger(MyProfileController.class);

    @Autowired
    MyProfileService myProfileService;

    @GetMapping
    public Mono<UserProfileDto> getMyProfile(ServerWebExchange exchange) {
        logger.debug("MyProfileController::getMyProfile");
        AuthnSession authnSession = validateAuthnSession(exchange);
        String userCId = authnSession.getUserId();
        return myProfileService.getMyProfile(userCId)
                .doOnSuccess(userProfile -> logger.info("Profile fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching profile"));
    }

    @PostMapping
    public Mono<UserProfileDto> updateProfile(ServerWebExchange exchange, @RequestBody UserProfileDto userProfileDto) {
        logger.debug("MyProfileController::updateProfile");
        AuthnSession authnSession = validateAuthnSession(exchange);
        String userCId = authnSession.getUserId();
        return myProfileService.updateProfile(userCId, userProfileDto)
                .doOnSuccess(userProfile -> logger.info("Profile updated successfully"))
                .doOnError(throwable -> logger.error("Error updating profile"));
    }
}
