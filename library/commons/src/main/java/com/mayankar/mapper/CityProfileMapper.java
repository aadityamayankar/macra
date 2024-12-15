package com.mayankar.mapper;

import com.mayankar.dto.CityProfileDto;
import com.mayankar.model.CityProfile;
import com.mayankar.util.CompositeID;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CityProfileMapper {
    CityProfileMapper INSTANCE = Mappers.getMapper(CityProfileMapper.class);

    @Named("fromCId")
    static Long fromCId(String cId) {
        return CompositeID.parseIdString(cId);
    }

    @Named("toCId")
    static String toCId(Long Id) {
        return CompositeID.parseId(Id);
    }

    @Mapping(target = "id", expression = "java(cityProfileDto.getId() == null ? null : CityProfileMapper.fromCId(cityProfileDto.getId()))")
    CityProfile toCityProfile(CityProfileDto cityProfileDto);

    @Mapping(target = "id", qualifiedByName = "toCId")
    @InheritInverseConfiguration(name = "toCityProfile")
    CityProfileDto toCityProfileDto(CityProfile cityProfile);
}