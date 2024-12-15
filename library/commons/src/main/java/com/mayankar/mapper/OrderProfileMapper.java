package com.mayankar.mapper;

import com.mayankar.dto.OrderProfileDto;
import com.mayankar.model.OrderProfile;
import com.mayankar.util.CompositeID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderProfileMapper {
    OrderProfileMapper INSTANCE = Mappers.getMapper(OrderProfileMapper.class);

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

    @Mapping(target = "userId", qualifiedByName = "toCId")
    @Mapping(target = "eventId", qualifiedByName = "toCId")
    @Mapping(target = "ticketId", qualifiedByName = "toCId")
    OrderProfileDto toOrderProfileDto(OrderProfile orderProfile);
}
