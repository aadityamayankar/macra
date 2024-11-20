package com.mayankar.mapper;

import com.mayankar.dto.UserProfileDto;
import com.mayankar.model.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    UserProfileDto toUserProfileDto(UserProfile userProfile);

    UserProfile toUserProfile(UserProfileDto userProfileDto);
}