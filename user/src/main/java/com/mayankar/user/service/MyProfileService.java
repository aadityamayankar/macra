package com.mayankar.user.service;

import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.dto.UserProfileDto;
import com.mayankar.mapper.UserProfileMapper;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

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

    public Mono<UserProfileDto> updateProfile(String userCId, UserProfileDto userProfileDto) {
        logger.info("MyProfileService::updateProfile");
        return userProfileRepository.getUserProfileById(CompositeID.parseIdString(userCId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("User profile not found")))
                .flatMap(existingUserProfile -> {
                    existingUserProfile.setName(userProfileDto.getName());
                    existingUserProfile.setEmail(userProfileDto.getEmail());
                    existingUserProfile.setModifiedAt(Instant.now());
                    logger.info("Updating user profile {}", existingUserProfile);
                    return userProfileRepository.save(existingUserProfile);
                })
                .map(userProfileMapper::toUserProfileDto);
    }
}
