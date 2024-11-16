package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.opsadmin.dto.EventProfileDto;
import com.mayankar.opsadmin.mapper.EventProfileMapper;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class EventProfileService {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileService.class);

    @Autowired
    EventProfileRepository eventProfileRepository;

    @Autowired
    EventProfileMapper eventProfileMapper;

    public Flux<EventProfileDto> getAllEventProfiles() {
        logger.info("EventProfileService::getAllEventProfiles");
        return eventProfileRepository.findAll()
                .map(eventProfileMapper::toEventProfileDto);
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
