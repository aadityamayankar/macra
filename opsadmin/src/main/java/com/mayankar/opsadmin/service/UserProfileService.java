package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.model.UserProfile;
import com.mayankar.opsadmin.dto.UserProfileDto;
import com.mayankar.opsadmin.mapper.UserProfileMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    UserProfileRepository userProfileRepository;

    @Autowired
    UserProfileMapper userProfileMapper;

    public Flux<UserProfileDto> getUserProfiles() {
        logger.info("UserProfileService::getUserProfiles");
        String query = UserProfileRepository.getAllUserProfiles;
        return userProfileRepository.search(query, UserProfile.class)
                .map(userProfileMapper::toUserProfileDto);
    }
}
