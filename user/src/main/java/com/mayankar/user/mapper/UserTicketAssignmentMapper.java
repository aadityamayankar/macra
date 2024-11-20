package com.mayankar.user.mapper;

import com.mayankar.model.UserTicketAssignment;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.util.CompositeID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserTicketAssignmentMapper {
    UserTicketAssignmentMapper INSTANCE = Mappers.getMapper(UserTicketAssignmentMapper.class);

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

    @Mapping(target = "id", expression = "java(userTicketAssignmentDto.getId() == null ? null : UserTicketAssignmentMapper.fromCId(userTicketAssignmentDto.getId()))")
    @Mapping(target = "userId", qualifiedByName = "fromCId")
    @Mapping(target = "ticketId", qualifiedByName = "fromCId")
    UserTicketAssignment toUserTicketAssignment(UserTicketAssignmentDto userTicketAssignmentDto);

    @Mapping(target = "id", qualifiedByName = "toCId")
    @Mapping(target = "userId", qualifiedByName = "toCId")
    @Mapping(target = "ticketId", qualifiedByName = "toCId")
    UserTicketAssignmentDto toUserTicketAssignmentDto(UserTicketAssignment userTicketAssignment);

    List<UserTicketAssignment> toUserTicketAssignmentList(List<UserTicketAssignmentDto> userTicketAssignmentDtoList);

    List<UserTicketAssignmentDto> toUserTicketAssignmentDtoList(List<UserTicketAssignment> userTicketAssignmentList);
}
