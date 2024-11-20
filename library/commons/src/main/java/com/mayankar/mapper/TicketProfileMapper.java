package com.mayankar.mapper;

import com.mayankar.dto.TicketProfileDto;
import com.mayankar.model.TicketProfile;
import com.mayankar.util.CompositeID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TicketProfileMapper {
    TicketProfileMapper INSTANCE = Mappers.getMapper(TicketProfileMapper.class);

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

    @Mapping(target = "id", expression = "java(ticketProfileDto.getId() == null ? null : TicketProfileMapper.fromCId(ticketProfileDto.getId()))")
    @Mapping(target = "eventId", expression = "java(ticketProfileDto.getEventId() == null ? null : TicketProfileMapper.fromCId(ticketProfileDto.getEventId()))")
    TicketProfile toTicketProfile(TicketProfileDto ticketProfileDto);

    @Mapping(target = "id", qualifiedByName = "toCId")
    @Mapping(target = "eventId", qualifiedByName = "toCId")
    TicketProfileDto toTicketProfileDto(TicketProfile ticketProfile);

    List<TicketProfile> toTicketProfileList(List<TicketProfileDto> ticketProfileDtoList);

    List<TicketProfileDto> toTicketProfileDtoList(List<TicketProfile> ticketProfileList);
}
