package com.mayankar.user.service;

import com.mayankar.dataaccess.repository.OrderProfileRepository;
import com.mayankar.mapper.OrderProfileMapper;
import com.mayankar.model.OrderProfile;
import com.mayankar.user.dto.OrderHistoryResponseDto;
import com.mayankar.util.CompositeID;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderProfileService {
    private static final Logger logger = LoggerFactory.getLogger(OrderProfileService.class);
    private final OrderProfileRepository orderProfileRepository;
    private final OrderProfileMapper orderProfileMapper;
    private final EventProfileService eventProfileService;

    public OrderProfileService(OrderProfileRepository orderProfileRepository, OrderProfileMapper orderProfileMapper, EventProfileService eventProfileService) {
        this.orderProfileRepository = orderProfileRepository;
        this.orderProfileMapper = orderProfileMapper;
        this.eventProfileService = eventProfileService;
    }

    public Flux<OrderHistoryResponseDto> getOrderHistory(String userCId) {
        logger.debug("OrderProfileService::getOrderHistory for user: {}", userCId);
        Long userId = CompositeID.parseIdString(userCId);
        return orderProfileRepository.getOrderProfilesByUserId(userId)
                .collectList()
                .flatMap(orderProfiles -> {
                    Map<Long, List<OrderProfile>> orderProfilesByEventId = new HashMap<>();
                    orderProfiles.forEach(orderProfile -> {
                        Long eventId = orderProfile.getEventId();
                        orderProfilesByEventId.computeIfAbsent(eventId, k -> new java.util.ArrayList<>()).add(orderProfile);
                    });
                    return Mono.just(orderProfilesByEventId);
                })
                .flatMapMany(orderProfilesByEventId -> {
                    return Flux.fromIterable(orderProfilesByEventId.keySet())
                            .flatMap(eventId -> {
                                String eventCId = CompositeID.parseId(eventId);
                                return eventProfileService.getEventProfileById(eventCId)
                                        .flatMap(eventProfileDto -> {
                                            Double totalAmount = 0.0;
                                            Map<String, String> ticketTypeByTicketId = new HashMap<>();
                                            Map<String, Integer> ticketCountByTicketType = new HashMap<>();

                                            eventProfileDto.getTickets().forEach(ticketProfileDto -> {
                                                ticketTypeByTicketId.put(ticketProfileDto.getId(), ticketProfileDto.getTicketType());
                                            });

                                            List<OrderProfile> orderProfiles = orderProfilesByEventId.get(eventId);
                                            for (OrderProfile orderProfile : orderProfiles) {
                                                totalAmount += orderProfile.getTotalAmount();
                                                String ticketType = ticketTypeByTicketId.get(CompositeID.parseId(orderProfile.getTicketId()));
                                                ticketCountByTicketType.put(ticketType, ticketCountByTicketType.getOrDefault(ticketType, 0) + orderProfile.getQuantity());
                                            }
                                            OrderHistoryResponseDto orderHistoryResponseDto = OrderHistoryResponseDto.builder()
                                                    .eventId(eventCId)
                                                    .eventCover(eventProfileDto.getCover())
                                                    .eventName(eventProfileDto.getName())
                                                    .eventStartDate(eventProfileDto.getStartDate())
                                                    .eventEndDate(eventProfileDto.getEndDate())
                                                    .eventLocation(eventProfileDto.getLocation())
                                                    .totalAmount(totalAmount)
                                                    .tickets(ticketCountByTicketType.entrySet().stream()
                                                            .map(entry -> new ImmutablePair<>(entry.getKey(), entry.getValue()))
                                                            .toList())
                                                    .build();
                                            return Mono.just(orderHistoryResponseDto);
                                        });
                            });
                });
    }
}
