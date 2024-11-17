package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.model.EventProfile;
import com.mayankar.opsadmin.dto.EventProfileDto;
import com.mayankar.opsadmin.dto.EventsRequestDto;
import com.mayankar.opsadmin.mapper.EventProfileMapper;
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

import java.sql.Date;
import java.time.Instant;

@Service
public class EventProfileService {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileService.class);

    @Autowired
    EventProfileRepository eventProfileRepository;

    @Autowired
    EventProfileMapper eventProfileMapper;

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

    public Mono<EventProfileDto> getEventProfileById(String id) {
        logger.info("EventProfileService::getEventProfileById");
        Long cId = CompositeID.parseIdString(id);
        return eventProfileRepository.getEventProfileById(cId)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event profile with id " + id + " not found")))
                .map(eventProfileMapper::toEventProfileDto);
    }

    public Mono<EventProfileDto> createEventProfile(EventProfileDto eventProfileDto) {
        logger.info("EventProfileService::createEventProfile");
        return validateEventProfile(eventProfileDto)
                .flatMap(eventProfileDto1 -> {
                    return eventProfileRepository.getEventProfileByNameAndCityId(eventProfileDto1.getName(), CompositeID.parseIdString(eventProfileDto1.getCityId()))
                            .flatMap(existingEventProfile -> Mono.error(new IllegalArgumentException("Event profile with name " + eventProfileDto1.getName() + " already exists in city " + eventProfileDto1.getCityId())))
                            .switchIfEmpty(eventProfileRepository.save(eventProfileMapper.toEventProfile(eventProfileDto1)));
                }).thenReturn(eventProfileDto);
    }

    private Mono<EventProfileDto> validateEventProfile(EventProfileDto eventProfileDto) {
        logger.info("EventProfileService::validateEventProfile");
        if (eventProfileDto.getStartDate().compareTo(eventProfileDto.getEndDate()) > 0) {
            return Mono.error(new IllegalArgumentException("Start date cannot be after end date"));
        }
        return Mono.just(eventProfileDto);
    }

    public Mono<Void> deleteEventProfile(String id) {
        logger.info("EventProfileService::deleteEventProfile");
        return getEventProfileById(id)
                .flatMap(eventProfileDto -> eventProfileRepository.delete(eventProfileMapper.toEventProfile(eventProfileDto)));
    }
}
