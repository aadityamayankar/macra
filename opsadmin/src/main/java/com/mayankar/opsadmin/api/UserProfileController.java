package com.mayankar.opsadmin.api;

import com.mayankar.controller.BaseController;
import com.mayankar.dto.UserProfileDto;
import com.mayankar.opsadmin.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/users")
public class UserProfileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    UserProfileService userProfileService;

    @GetMapping
    public Flux<UserProfileDto> getUserProfiles(ServerWebExchange exchange) {
        logger.debug("UserProfileController::getUserProfiles");
        return userProfileService.getUserProfiles();
    }
}
