package com.mayankar.user.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventsRequestDto;
import com.mayankar.mapper.EventProfileMapper;
import com.mayankar.mapper.TicketProfileMapper;
import com.mayankar.model.EventProfile;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
public class EventProfileService {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileService.class);

    @Autowired
    EventProfileRepository eventProfileRepository;

    @Autowired
    EventProfileMapper eventProfileMapper;
    @Autowired
    private TicketProfileRepository ticketProfileRepository;
    @Autowired
    private TicketProfileMapper ticketProfileMapper;

    public Flux<EventProfileDto> getAllEventProfiles(EventsRequestDto eventsRequestDto) {
        logger.info("EventProfileService::getAllEventProfiles");
        return validateEventsRequestDto(eventsRequestDto)
                .flatMapMany(eventsRequestDto1 -> {
                    Pair<String, MultiValueMap<String, ?>> queryAndBindings = getQueryAndBindings(eventsRequestDto1);
                    return eventProfileRepository.search(queryAndBindings.getFirst(), queryAndBindings.getSecond(), EventProfile.class)
                            .map(eventProfileMapper::toEventProfileDto);
                });
    }

    private Mono<EventsRequestDto> validateEventsRequestDto(EventsRequestDto eventsRequestDto) {
        logger.info("EventProfileService::validateEventsRequestDto");
        if (eventsRequestDto.getStartDate() != null && eventsRequestDto.getEndDate() != null) {
            if (eventsRequestDto.getStartDate().compareTo(eventsRequestDto.getEndDate()) > 0) {
                return Mono.error(new IllegalArgumentException("Start date cannot be after end date"));
            }
        }
        return Mono.just(eventsRequestDto);
    }

    private Pair<String, MultiValueMap<String, ?>> getQueryAndBindings(EventsRequestDto eventsRequestDto) {
        logger.info("EventProfileService::getQueryAndBindings");
        StringBuilder query = new StringBuilder(EventProfileRepository.getAllEventProfilesWithCity);
        MultiValueMap<String, Object> bindings = new LinkedMultiValueMap<>();
        if (!ObjectUtils.isEmpty(eventsRequestDto)) {
            if (eventsRequestDto.getCity() != null) {
                addQueryParam(query, EventProfileRepository.withCityName, "city", eventsRequestDto.getCity(), bindings);
            }
            if (eventsRequestDto.getName() != null) {
                addQueryParam(query, EventProfileRepository.withName, "name", eventsRequestDto.getName(), bindings);
            }
            if (eventsRequestDto.getStartDate() != null) {
                addQueryParam(query, EventProfileRepository.withStartDate, "startDate", Instant.ofEpochSecond(Long.parseLong(eventsRequestDto.getStartDate())), bindings);
            }
            if (eventsRequestDto.getEndDate() != null) {
                addQueryParam(query, EventProfileRepository.withEndDate, "endDate", Instant.ofEpochSecond(Long.parseLong(eventsRequestDto.getEndDate())), bindings);
            }
            if (eventsRequestDto.getDeleted() != null) {
                if (!bindings.isEmpty()) {
                    query.append(EventProfileRepository.AND);
                } else {
                    query.append(EventProfileRepository.WHERE);
                }
                if (eventsRequestDto.getDeleted()) {
                    query.append(EventProfileRepository.withDeleted);
                } else {
                    query.append(EventProfileRepository.withNotDeleted);
                }
            }
        }
        return Pair.of(query.toString(), bindings);
    }

    private <T> void addQueryParam(StringBuilder query, String queryParam, String queryParamBinding, T value, MultiValueMap<String, T> bindings) {
        if (!bindings.isEmpty()) {
            query.append(EventProfileRepository.AND);
        } else {
            query.append(EventProfileRepository.WHERE);
        }
        query.append(queryParam);
        bindings.add(queryParamBinding, value);
    }

    public Mono<EventProfileDto> getEventProfileById(String cId) {
        logger.info("EventProfileService::getEventProfileById");
        Long id = CompositeID.parseIdString(cId);
        return eventProfileRepository.getEventProfileById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event profile with id " + id + " not found")))
                .flatMap(eventProfileWithCity -> {
                    return ticketProfileRepository.getTicketProfilesByEventId(id)
                            .collectList()
                            .map(ticketProfiles -> {
                                EventProfileDto eventProfileDto = eventProfileMapper.toEventProfileDto(eventProfileWithCity);
                                eventProfileDto.setTickets(ticketProfileMapper.toTicketProfileDtoList(ticketProfiles));
                                return eventProfileDto;
                            });
                });
    }

}
