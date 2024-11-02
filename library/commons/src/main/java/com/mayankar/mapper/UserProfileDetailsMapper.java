package com.mayankar.mapper;

import com.mayankar.dto.UserProfileWithDetails;
import com.mayankar.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserProfileDetailsMapper {
    UserProfileDetailsMapper INSTANCE = Mappers.getMapper(UserProfileDetailsMapper.class);

    @Mapping(target = "role", ignore = true)
    UserProfileWithDetails toUserProfileWithDetails(UserProfile userProfile);
}
