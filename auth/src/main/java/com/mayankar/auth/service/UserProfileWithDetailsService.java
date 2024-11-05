package com.mayankar.auth.service;

import com.mayankar.dataaccess.repository.RoleProfileRepository;
import com.mayankar.dto.UserProfileWithDetails;
import com.mayankar.mapper.UserProfileDetailsMapper;
import com.mayankar.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserProfileWithDetailsService {
    @Autowired
    RoleProfileRepository roleProfileRepository;

    public Mono<UserProfileWithDetails> getUserProfileWithDetails(UserProfile userProfile) {
        return roleProfileRepository.getRoleProfileByUserId(userProfile.getId())
                .map(roleProfile -> {
                    UserProfileWithDetails userProfileWithDetails = UserProfileDetailsMapper.INSTANCE.toUserProfileWithDetails(userProfile);
                    userProfileWithDetails.setRole(roleProfile);
                    return userProfileWithDetails;
                });
    }
}
