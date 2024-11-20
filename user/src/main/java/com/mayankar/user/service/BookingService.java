package com.mayankar.user.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.dataaccess.repository.UserTicketAssignmentRepository;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.mapper.EventProfileMapper;
import com.mayankar.model.UserTicketAssignment;
import com.mayankar.user.dto.Status;
import com.mayankar.user.dto.TicketBookingRequest;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final UserTicketAssignmentRepository userTicketAssignmentRepository;
    private final EventProfileRepository eventProfileRepository;
    private final EventProfileMapper eventProfileMapper;
    private final TicketProfileRepository ticketProfileRepository;

    public BookingService(UserTicketAssignmentRepository userTicketAssignmentRepository, EventProfileRepository eventProfileRepository, EventProfileMapper eventProfileMapper, TicketProfileRepository ticketProfileRepository) {
        this.userTicketAssignmentRepository = userTicketAssignmentRepository;
        this.eventProfileRepository = eventProfileRepository;
        this.eventProfileMapper = eventProfileMapper;
        this.ticketProfileRepository = ticketProfileRepository;
    }

    public Flux<UserTicketAssignmentDto> getBookingHistory(String userCId) {
        logger.info("BookingService::getBookingHistory");
        Long userId = CompositeID.parseIdString(userCId);
        return userTicketAssignmentRepository.getUserTicketAssignmentByUserId(userId)
                .flatMap(userTicketAssignment -> {
                    return eventProfileRepository.getEventProfileById(userTicketAssignment.getEventId())
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                            .flatMap(eventProfileWithCity -> getUserTicketAssignmentDtoWithEventProfile(eventProfileWithCity, userTicketAssignment))
                            .doOnError(throwable -> logger.error("Error fetching event profile"));
                })
                .doOnError(throwable -> logger.error("Error fetching user ticket assignment"));
    }

    //@TODO: look into atomicity of this transaction
    @Transactional
    public Mono<UserTicketAssignmentDto> bookTicket(String userCId, TicketBookingRequest ticketBookingRequest) {
        logger.info("BookingService::bookTicket for user {}, ticket booking request {}", userCId, ticketBookingRequest);
        Long userId = CompositeID.parseIdString(userCId);
        return eventProfileRepository.getEventProfileById(CompositeID.parseIdString(ticketBookingRequest.getEventId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .flatMap(eventProfileWithCity -> {
                    return ticketProfileRepository.getTicketProfilesByEventId(eventProfileWithCity.getId())
                            .filter(ticketProfile -> ticketProfile.getId().equals(CompositeID.parseIdString(ticketBookingRequest.getTicketId())))
                            .singleOrEmpty()
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Ticket not found")))
                            .flatMap(ticketProfile -> {
                                if (ticketProfile.getAvailableQuantity() < ticketBookingRequest.getQuantity()) {
                                    return Mono.error(new IllegalArgumentException("Not enough tickets available"));
                                }
                                logger.info("Ticket available for booking");
                                UserTicketAssignment userTicketAssignment = UserTicketAssignment.builder()
                                        .userId(userId)
                                        .ticketId(ticketProfile.getId())
                                        .eventId(eventProfileWithCity.getId())
                                        .quantity(ticketBookingRequest.getQuantity())
                                        .build();
                                return userTicketAssignmentRepository.save(userTicketAssignment)
                                        .flatMap(userTicketAssignment1 -> {
                                            ticketProfile.setAvailableQuantity(ticketProfile.getAvailableQuantity() - ticketBookingRequest.getQuantity());
                                            return ticketProfileRepository.save(ticketProfile)
                                                    .flatMap(ticketProfile1 -> getUserTicketAssignmentDtoWithEventProfile(eventProfileWithCity, userTicketAssignment1));
                                        });
                            });
                });
    }

    private Mono<UserTicketAssignmentDto> getUserTicketAssignmentDtoWithEventProfile(EventProfileWithCity eventProfileWithCity, UserTicketAssignment userTicketAssignment) {
        EventProfileDto eventProfileDto = eventProfileMapper.toEventProfileDto(eventProfileWithCity);
        UserTicketAssignmentDto userTicketAssignmentDto = UserTicketAssignmentDto.builder()
                .id(CompositeID.parseId(userTicketAssignment.getId()))
                .userId(CompositeID.parseId(userTicketAssignment.getUserId()))
                .ticketId(CompositeID.parseId(userTicketAssignment.getTicketId()))
                .quantity(userTicketAssignment.getQuantity())
                .event(eventProfileDto)
                .status(getStatus(eventProfileWithCity))
                .build();
        return Mono.just(userTicketAssignmentDto);
    }

    private Status getStatus(EventProfileWithCity eventProfileWithCity) {
        if (Instant.now().isBefore(eventProfileWithCity.getStartDate())) {
            return Status.UPCOMING;
        } else if (Instant.now().isAfter(eventProfileWithCity.getEndDate())) {
            return Status.PAST;
        } else {
            return Status.LIVE;
        }
    }
}
