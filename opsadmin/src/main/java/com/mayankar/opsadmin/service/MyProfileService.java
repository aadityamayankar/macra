package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.opsadmin.dto.UserProfileDto;
import com.mayankar.opsadmin.mapper.UserProfileMapper;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MyProfileService {
    private static final Logger logger = LoggerFactory.getLogger(MyProfileService.class);

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserProfileMapper userProfileMapper;

    public Mono<UserProfileDto> getMyProfile(String userCId) {
        logger.info("MyProfileService::getMyProfile");
        return userProfileRepository.getUserProfileById(CompositeID.parseIdString(userCId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User profile not found")))
                .map(userProfileMapper::toUserProfileDto);
    }
}
