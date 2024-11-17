package com.mayankar.opsadmin.mapper;

import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.model.EventProfile;
import com.mayankar.model.UserProfile;
import com.mayankar.opsadmin.dto.EventProfileDto;
import com.mayankar.opsadmin.dto.UserProfileDto;
import com.mayankar.util.CompositeID;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    UserProfileDto toUserProfileDto(UserProfile userProfile);

    UserProfile toUserProfile(UserProfileDto userProfileDto);
}