package com.mayankar.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayankar.dataaccess.cachedrepository.TicketLockRepository;
import com.mayankar.dataaccess.cachedrepository.TicketProfileCacheRepository;
import com.mayankar.dataaccess.cachedrepository.PaymentOrderReservationMappingRepository;
import com.mayankar.dataaccess.cachedrepository.TicketReservationRepository;
import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.dataaccess.repository.UserTicketAssignmentRepository;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventProfileWithCity;
import com.mayankar.mapper.EventProfileMapper;
import com.mayankar.model.PaymentOrderReservationMapping;
import com.mayankar.model.TicketProfile;
import com.mayankar.model.TicketReservation;
import com.mayankar.model.UserTicketAssignment;
import com.mayankar.user.dto.RazorPayOrderDto;
import com.mayankar.user.dto.Status;
import com.mayankar.user.dto.TicketBookingRequest;
import com.mayankar.user.dto.UserTicketAssignmentDto;
import com.mayankar.user.sender.EventSyncSender;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.time.Instant;

import static com.mayankar.util.Constants.MAX_TICKET_BOOKING_RETRIES;
import static com.mayankar.util.Constants.TICKET_BOOKING_RETRY_INTERVAL;

@Service
public class TicketBookingService {
    private static final Logger logger = LoggerFactory.getLogger(TicketBookingService.class);

    @Autowired
    UserTicketAssignmentRepository userTicketAssignmentRepository;

    @Autowired
    EventProfileRepository eventProfileRepository;

    @Autowired
    EventProfileMapper eventProfileMapper;

    @Autowired
    TicketLockRepository ticketLockRepository;

    @Autowired
    TicketProfileCacheRepository ticketProfileCacheRepository;

    @Autowired
    TicketReservationRepository ticketReservationRepository;

    @Autowired
    RazorpayPaymentService razorpayPaymentService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private PaymentOrderReservationMappingRepository paymentOrderReservationMappingRepository;

    @Autowired
    private TicketProfileRepository ticketProfileRepository;

    @Autowired
    private EventSyncSender eventSyncSender;

    public Flux<UserTicketAssignmentDto> getBookingHistory(String userCId) {
        logger.info("TicketBookingService::getBookingHistory");
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

    public Mono<RazorPayOrderDto> bookTicket(String userCId, TicketBookingRequest ticketBookingRequest) {
        logger.info("TicketBookingService::bookTicket for user {}, ticket booking request {}", userCId, ticketBookingRequest);
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

    private Mono<RazorPayOrderDto> bookTicketWithRetry(String userCId, TicketProfile ticketProfile, EventProfileWithCity eventProfileWithCity, TicketBookingRequest ticketBookingRequest) {
        logger.info("TicketBookingService::bookTicketWithRetry for user {}, ticket profile {}, ticket booking request {}", userCId, ticketProfile, ticketBookingRequest);
        return attemptBooking(userCId, ticketProfile, eventProfileWithCity, ticketBookingRequest, 0)
                .doOnError(throwable -> logger.error("Error booking ticket"));
    }

    private Mono<RazorPayOrderDto> attemptBooking(String userCId, TicketProfile ticketProfile, EventProfileWithCity eventProfileWithCity, TicketBookingRequest ticketBookingRequest, int retryCount) {
        logger.info("TicketBookingService::attemptBooking for user {}, ticket profile {}, ticket booking request {}, retry count {}", userCId, ticketProfile, ticketBookingRequest, retryCount);
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
                    final String eventCID = CompositeID.parseId(eventProfileWithCity.getId());
                    final String ticketProfileCID = CompositeID.parseId(ticketProfile.getId());
                    final String ticketReservationKey = ticketReservationRepository.getTicketReservationKey(eventCID, ticketProfileCID);
                    return ticketProfileCacheRepository.getTicketProfile(ticketProfileCID)
                            .zipWith(ticketReservationRepository.getReservedQuantity(ticketReservationKey))
                            .flatMap(tuple2 -> {
                                TicketProfile ticketProfile1 = tuple2.getT1();
                                Integer reservedQuantity = tuple2.getT2();
                                if (ticketProfile1.getAvailableQuantity() - reservedQuantity < ticketBookingRequest.getQuantity()) {
                                    logger.error("Not enough tickets available. Available: {}, Reserved: {}, Requested: {}", ticketProfile1.getAvailableQuantity(), reservedQuantity, ticketBookingRequest.getQuantity());
                                    return Mono.error(new IllegalArgumentException("Not enough tickets available"));
                                }
                                TicketReservation ticketReservation = TicketReservation.builder()
                                        .userId(userCId)
                                        .eventId(eventCID)
                                        .ticketId(ticketProfileCID)
                                        .reservedQuantity(ticketBookingRequest.getQuantity())
                                        .totalAmount(ticketProfile.getPrice() * ticketBookingRequest.getQuantity())
                                        .createdAt(Instant.now())
                                        .build();

                                return ticketReservationRepository.createTicketReservation(ticketReservationKey, ticketReservation)
                                        .then(processBooking(userCId, ticketReservation));
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

    private Mono<RazorPayOrderDto> processBooking(String userCId, TicketReservation ticketReservation) {
        logger.info("TicketBookingService::processBooking for user {}, ticket reservation {}", userCId, ticketReservation);

        return razorpayPaymentService.initiatePayment(userCId, ticketReservation)
                .<RazorPayOrderDto>handle((order, sink) -> {
                    try {
                        sink.next(objectMapper.readValue(order.toJson().toString(), RazorPayOrderDto.class));
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .flatMap(razorpayOrder -> {
                    PaymentOrderReservationMapping paymentOrderReservationMapping = PaymentOrderReservationMapping.builder()
                            .reservationId(ticketReservation.getId())
                            .ticketId(ticketReservation.getTicketId())
                            .eventId(ticketReservation.getEventId())
                            .build();
                    return paymentOrderReservationMappingRepository.savePaymentOrderReservationMapping(razorpayOrder.getId(), paymentOrderReservationMapping)
                            .thenReturn(razorpayOrder);
                })
                .doOnError(throwable -> logger.error("Error initiating payment"));
    }

    public Mono<Tuple2<UserTicketAssignmentDto, TicketReservation>> finalizeBooking(String orderId, String paymentId) {
        logger.info("TicketBookingService::finalizeBooking for orderId {}, paymentId {}", orderId, paymentId);
        return paymentOrderReservationMappingRepository.getTicketReservationId(orderId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Reservation not found")))
                .flatMap(paymentOrderReservationMapping -> {
                    String ticketReservationKey = ticketReservationRepository.getTicketReservationKey(paymentOrderReservationMapping.getEventId(), paymentOrderReservationMapping.getTicketId());
                    return ticketReservationRepository.getTicketReservationById(ticketReservationKey, paymentOrderReservationMapping.getReservationId())
                            .flatMap(ticketReservation -> {
                                return ticketProfileRepository.getTicketProfileById(CompositeID.parseIdString(ticketReservation.getTicketId()))
                                        .switchIfEmpty(Mono.error(new IllegalArgumentException("Ticket not found")))
                                        .flatMap(ticketProfile -> {
                                            UserTicketAssignment userTicketAssignment = UserTicketAssignment.builder()
                                                    .userId(CompositeID.parseIdString(ticketReservation.getUserId()))
                                                    .ticketId(CompositeID.parseIdString(ticketReservation.getTicketId()))
                                                    .eventId(CompositeID.parseIdString(ticketReservation.getEventId()))
                                                    .quantity(ticketReservation.getReservedQuantity())
                                                    .build();
                                            return eventProfileRepository.getEventProfileById(CompositeID.parseIdString(ticketReservation.getEventId()))
                                                    .switchIfEmpty(Mono.error(new IllegalArgumentException("Event not found")))
                                                    .flatMap(eventProfileWithCity -> {
                                                        logger.info("Assigning {} tickets to user: {}, ticket: {}, event: {}", ticketReservation.getReservedQuantity(), userTicketAssignment, ticketProfile, eventProfileWithCity);
                                                        return userTicketAssignmentRepository.save(userTicketAssignment)
                                                                .flatMap(userTicketAssignment1 -> {
                                                                    ticketProfile.setAvailableQuantity(ticketProfile.getAvailableQuantity() - ticketReservation.getReservedQuantity());
                                                                    ticketProfile.setModifiedAt(Instant.now());
                                                                    return ticketProfileRepository.save(ticketProfile)
                                                                            .flatMap(ticketProfile1 -> {
                                                                                String ticketReservationKey1 = ticketReservationRepository.getTicketReservationKey(ticketReservation.getEventId(), ticketReservation.getTicketId());
                                                                                return ticketReservationRepository.deleteTicketReservation(ticketReservationKey1, ticketReservation.getId())
                                                                                        .then(eventSyncSender.sendEventSyncMessage(CompositeID.parseId(ticketProfile.getEventId()), CompositeID.parseId(ticketProfile.getId()), userTicketAssignment1.getQuantity(), Instant.now()))
                                                                                        .then(getUserTicketAssignmentDtoWithEventProfile(eventProfileWithCity, userTicketAssignment1)
                                                                                                .zipWith(Mono.just(ticketReservation)));
                                                                            });
                                                                });
                                                    });
                                        });
                            });
                })
                .doOnError(throwable -> logger.error("Error finalizing booking"));
    }

    // Don't release reservation if the payment fails as it may be completed successfully later. The reservation will be cleaned up by the scheduled job
    public Mono<Boolean> releaseReservation(String orderId) {
        logger.info("TicketBookingService::releaseReservation for orderId {}", orderId);
        return paymentOrderReservationMappingRepository.getTicketReservationId(orderId)
                .flatMap(paymentOrderReservationMapping -> {
                    String ticketReservationKey = ticketReservationRepository.getTicketReservationKey(paymentOrderReservationMapping.getEventId(), paymentOrderReservationMapping.getTicketId());
                    return ticketReservationRepository.deleteTicketReservation(ticketReservationKey, paymentOrderReservationMapping.getReservationId());
                })
                .doOnError(throwable -> logger.error("Error releasing reservation"));
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
