package com.mayankar.opsadmin.mapper;

import com.mayankar.model.EventProfile;
import com.mayankar.opsadmin.dto.EventProfileDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EventProfileMapper {
    EventProfileMapper INSTANCE = Mappers.getMapper(EventProfileMapper.class);

    EventProfile toEventProfile(EventProfileDto eventProfileDto);
    EventProfileDto toEventProfileDto(EventProfile eventProfile);
}
