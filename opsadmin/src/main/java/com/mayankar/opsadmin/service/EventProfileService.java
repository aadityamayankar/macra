package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.model.EventProfile;
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

    public Flux<EventProfile> getAllEventProfiles() {
        logger.debug("EventProfileService::getAllEventProfiles");
        return eventProfileRepository.findAll();
    }

    public Mono<EventProfile> getEventProfileById(String id) {
        logger.debug("EventProfileService::getEventProfileById");
        Long cId = CompositeID.parseIdString(id);
        return eventProfileRepository.findById(cId);
    }

    public Mono<EventProfileDto> createEventProfile(EventProfileDto eventProfileDto) {
        logger.debug("EventProfileService::createEventProfile");
        //@TODO: clean this up
        return eventProfileRepository.save(EventProfileMapper.INSTANCE.toEventProfile(eventProfileDto))
                .flatMap(eventProfile -> Mono.just(EventProfileMapper.INSTANCE.toEventProfileDto(eventProfile)));
    }

//    public Mono<Void> deleteEventProfile(String id) {
//        logger.debug("EventProfileService::deleteEventProfile");
//        Long cId = CompositeID.parseIdString(id);
//
//    }
}
