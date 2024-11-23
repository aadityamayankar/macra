package com.mayankar.user.service;

import com.mayankar.dataaccess.cachedrepository.TicketLockRepository;
import com.mayankar.dataaccess.cachedrepository.TicketProfileCacheRepository;
import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.dataaccess.repository.UserTicketAssignmentRepository;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.mapper.EventProfileMapper;
import com.mayankar.model.TicketProfile;
import com.mayankar.model.UserTicketAssignment;
import com.mayankar.user.dto.Status;
import com.mayankar.user.dto.TicketBookingRequest;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.user.sender.EventSyncSender;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;

import static com.mayankar.util.Constants.MAX_TICKET_BOOKING_RETRIES;
import static com.mayankar.util.Constants.TICKET_BOOKING_RETRY_INTERVAL;

@Service
public class BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingService.class);
    private final UserTicketAssignmentRepository userTicketAssignmentRepository;
    private final EventProfileRepository eventProfileRepository;
    private final EventProfileMapper eventProfileMapper;
    private final TicketProfileRepository ticketProfileRepository;
    private final TicketLockRepository ticketLockRepository;
    private final TicketProfileCacheRepository ticketProfileCacheRepository;
    private final EventSyncSender eventSyncSender;

    public BookingService(UserTicketAssignmentRepository userTicketAssignmentRepository, EventProfileRepository eventProfileRepository, EventProfileMapper eventProfileMapper, TicketProfileRepository ticketProfileRepository, TicketLockRepository ticketLockRepository, TicketProfileCacheRepository ticketProfileCacheRepository, EventSyncSender eventSyncSender) {
        this.userTicketAssignmentRepository = userTicketAssignmentRepository;
        this.eventProfileRepository = eventProfileRepository;
        this.eventProfileMapper = eventProfileMapper;
        this.ticketProfileRepository = ticketProfileRepository;
        this.ticketLockRepository = ticketLockRepository;
        this.ticketProfileCacheRepository = ticketProfileCacheRepository;
        this.eventSyncSender = eventSyncSender;
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

    @Transactional
    public Mono<UserTicketAssignmentDto> bookTicket(String userCId, TicketBookingRequest ticketBookingRequest) {
        logger.info("BookingService::bookTicket for user {}, ticket booking request {}", userCId, ticketBookingRequest);
        Long userId = CompositeID.parseIdString(userCId);
        return eventProfileRepository.getEventProfileById(CompositeID.parseIdString(ticketBookingRequest.getEventId()))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                .flatMap(eventProfileWithCity -> {
                    return ticketProfileCacheRepository.getTicketProfilesByEventId(eventProfileWithCity.getId())
                            .filter(ticketProfile -> ticketProfile.getId().equals(CompositeID.parseIdString(ticketBookingRequest.getTicketId())))
                            .singleOrEmpty()
                            .switchIfEmpty(Mono.error(new IllegalArgumentException("Ticket not found")))
                            .flatMap(ticketProfile -> bookTicketWithRetry(userCId, ticketProfile, eventProfileWithCity, ticketBookingRequest));
                });
    }

    private Mono<UserTicketAssignmentDto> bookTicketWithRetry(String userCId, TicketProfile ticketProfile, EventProfileWithCity eventProfileWithCity, TicketBookingRequest ticketBookingRequest) {
        logger.info("BookingService::bookTicketWithRetry for user {}, ticket profile {}, ticket booking request {}", userCId, ticketProfile, ticketBookingRequest);
        return attemptBooking(userCId, ticketProfile, eventProfileWithCity, ticketBookingRequest, 0)
                .doOnError(throwable -> logger.error("Error booking ticket"));
    }

    private Mono<UserTicketAssignmentDto> attemptBooking(String userCId, TicketProfile ticketProfile, EventProfileWithCity eventProfileWithCity, TicketBookingRequest ticketBookingRequest, int retryCount) {
        logger.info("BookingService::attemptBooking for user {}, ticket profile {}, ticket booking request {}, retry count {}", userCId, ticketProfile, ticketBookingRequest, retryCount);
        final String lockKey = ticketLockRepository.generateLockKey(ticketProfile.getEventId().toString(), ticketProfile.getId().toString());
        return ticketLockRepository.acquireLock(lockKey)
                .flatMap(acquired -> {
                    logger.debug("Lock acquired: {} for key {}", acquired, lockKey);
                    if (!acquired) {
                        if (retryCount < MAX_TICKET_BOOKING_RETRIES) {
                            return Mono.delay(Duration.ofMillis(TICKET_BOOKING_RETRY_INTERVAL))
                                    .then(attemptBooking(userCId, ticketProfile, eventProfileWithCity, ticketBookingRequest, retryCount + 1));
                        } else {
                            logger.error("Failed to acquire lock. Max retries reached");
                            return Mono.error(new IllegalArgumentException("Failed to book tickets. Please try again later"));
                        }
                    }
                    return ticketProfileCacheRepository.getTicketProfile(CompositeID.parseId(ticketProfile.getId()))
                            .flatMap(ticketProfile1 -> {
                                if (ticketProfile1.getAvailableQuantity() < ticketBookingRequest.getQuantity()) {
                                    return Mono.error(new IllegalArgumentException("Not enough tickets available"));
                                }
                                return processBooking(userCId, ticketProfile, eventProfileWithCity, ticketBookingRequest);
                            })
                            .publishOn(Schedulers.boundedElastic())
                            .doFinally(signalType -> {
                                logger.debug("Releasing lock for key {}", lockKey);
                                ticketLockRepository.releaseLock(lockKey)
                                        .subscribe(released -> {
                                            if (!released)
                                                logger.error("Failed to release lock for key {}", lockKey);
                                            else
                                                logger.debug("Lock released for key {}", lockKey);
                                        });
                            });
                });
    }

    private Mono<UserTicketAssignmentDto> processBooking(String userCId, TicketProfile ticketProfile, EventProfileWithCity eventProfileWithCity, TicketBookingRequest ticketBookingRequest) {
        logger.info("BookingService::processBooking for user {}, ticket profile {}, ticket booking request {}", userCId, ticketProfile, ticketBookingRequest);

        UserTicketAssignment userTicketAssignment = UserTicketAssignment.builder()
                .userId(CompositeID.parseIdString(userCId))
                .ticketId(ticketProfile.getId())
                .eventId(ticketProfile.getEventId())
                .quantity(ticketBookingRequest.getQuantity())
                .build();
        return userTicketAssignmentRepository.save(userTicketAssignment)
                .flatMap(userTicketAssignment1 -> {
                    ticketProfile.setAvailableQuantity(ticketProfile.getAvailableQuantity() - ticketBookingRequest.getQuantity());
                    ticketProfile.setModifiedAt(Instant.now());
                    return ticketProfileRepository.save(ticketProfile)
                            .flatMap(ticketProfile1 -> {
                                return eventSyncSender.sendEventSyncMessage(CompositeID.parseId(ticketProfile.getEventId()), CompositeID.parseId(ticketProfile.getId()), userTicketAssignment1.getQuantity(), Instant.now())
                                        .then(getUserTicketAssignmentDtoWithEventProfile(eventProfileWithCity, userTicketAssignment1));
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
