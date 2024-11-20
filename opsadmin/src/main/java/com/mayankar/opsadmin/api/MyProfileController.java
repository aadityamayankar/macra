package com.mayankar.opsadmin.api;

import com.mayankar.controller.BaseController;
import com.mayankar.dto.UserProfileDto;
import com.mayankar.model.AuthnSession;
import com.mayankar.opsadmin.service.MyProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/my-profile")
public class MyProfileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(MyProfileController.class);
    private final MyProfileService myProfileService;

    public MyProfileController(MyProfileService myProfileService) {
        this.myProfileService = myProfileService;
    }

    @GetMapping
    public Mono<UserProfileDto> getMyProfile(ServerWebExchange exchange) {
        logger.debug("MyProfileController::getMyProfile");
        AuthnSession authnSession = validateAuthnSession(exchange);
        String userCId = authnSession.getUserId();
        return myProfileService.getMyProfile(userCId)
                .doOnSuccess(userProfileDto -> logger.info("User profile retrieved successfully"))
                .doOnError(throwable -> logger.error("Error retrieving user profile", throwable));
    }
}
