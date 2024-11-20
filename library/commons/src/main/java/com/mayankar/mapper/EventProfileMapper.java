package com.mayankar.mapper;

import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.model.EventProfile;
import com.mayankar.util.CompositeID;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface EventProfileMapper {
    EventProfileMapper INSTANCE = Mappers.getMapper(EventProfileMapper.class);

    @Named("toInstant")
    static Instant toInstant(String date) {
        return Instant.ofEpochSecond(Long.parseLong(date));
    }

    @Named("fromInstant")
    static String fromInstant(Instant date) {
        return date.getEpochSecond() + "";
    }

    @Named("fromCId")
    static Long fromCId(String cId) {
        return CompositeID.parseIdString(cId);
    }

    @Named("toCId")
    static String toCId(Long Id) {
        return CompositeID.parseId(Id);
    }

    @Mapping(target = "startDate", qualifiedByName = "toInstant")
    @Mapping(target = "endDate", qualifiedByName = "toInstant")
    @Mapping(target = "id", expression = "java(eventProfileDto.getId() == null ? null : EventProfileMapper.fromCId(eventProfileDto.getId()))")
    @Mapping(target = "cityId", qualifiedByName = "fromCId")
    EventProfile toEventProfile(EventProfileDto eventProfileDto);

    @Mapping(target = "startDate", qualifiedByName = "fromInstant")
    @Mapping(target = "endDate", qualifiedByName = "fromInstant")
    @Mapping(target = "id", qualifiedByName = "toCId")
    @Mapping(target = "cityId", qualifiedByName = "toCId")
    @InheritInverseConfiguration(name = "toEventProfile")
    EventProfileDto toEventProfileDto(EventProfile eventProfile);

    @InheritConfiguration(name = "toEventProfileDto")
    EventProfileDto toEventProfileDto(EventProfileWithCity eventProfileWithCity);

    @InheritConfiguration(name = "toEventProfile")
    EventProfile toEventProfile(EventProfileWithCity eventProfileWithCity);
}