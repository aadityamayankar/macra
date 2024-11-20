package com.mayankar.opsadmin.service;

import com.mayankar.dataaccess.repository.EventProfileRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventsRequestDto;
import com.mayankar.dto.TicketProfileDto;
import com.mayankar.mapper.EventProfileMapper;
import com.mayankar.mapper.TicketProfileMapper;
import com.mayankar.model.EventProfile;
import com.mayankar.model.TicketProfile;
import com.mayankar.util.CompositeID;
import com.mayankar.util.Constants;
import com.mayankar.util.RepositoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventProfileService {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileService.class);

    @Autowired
    EventProfileRepository eventProfileRepository;

    @Autowired
    EventProfileMapper eventProfileMapper;

    @Autowired
    TicketProfileMapper ticketProfileMapper;

    @Autowired
    private TicketProfileRepository ticketProfileRepository;
    @Autowired
    private RepositoryUtils repositoryUtils;

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

    @Transactional
    public Mono<EventProfileDto> createEventProfile(EventProfileDto eventProfileDto) {
        logger.info("EventProfileService::createEventProfile");
        return validateEventProfile(eventProfileDto)
                .flatMap(eventProfileDto1 -> {
                    return eventProfileRepository.getAllEventProfileByNameAndCityId(eventProfileDto1.getName(), CompositeID.parseIdString(eventProfileDto1.getCityId()))
                            .flatMap(existingEventProfile -> {
                                if (!repositoryUtils.isDeleted(existingEventProfile)) {
                                    return Mono.error(new IllegalArgumentException("Event profile with name " + eventProfileDto1.getName() + " already exists in city " + eventProfileDto1.getCityId()));
                                }
                                existingEventProfile.setMiscflags(0L);
                                existingEventProfile.setName(eventProfileDto1.getName());
                                existingEventProfile.setStartDate(Instant.ofEpochSecond(Long.parseLong(eventProfileDto1.getStartDate())));
                                existingEventProfile.setEndDate(Instant.ofEpochSecond(Long.parseLong(eventProfileDto1.getEndDate())));
                                existingEventProfile.setLocation(eventProfileDto1.getLocation());
                                existingEventProfile.setCityId(CompositeID.parseIdString(eventProfileDto1.getCityId()));
                                existingEventProfile.setCreatedAt(Instant.now());
                                existingEventProfile.setModifiedAt(Instant.now());
                                return eventProfileRepository.save(existingEventProfile);
                            })
                            .switchIfEmpty(eventProfileRepository.save(eventProfileMapper.toEventProfile(eventProfileDto1))
                                    .flatMap(eventProfile -> {
                                        List<TicketProfileDto> ticketProfileDtos = eventProfileDto.getTickets();
                                        if (ticketProfileDtos != null && !ticketProfileDtos.isEmpty()) {
                                            return Flux.fromIterable(ticketProfileDtos)
                                                    .flatMap(ticketProfileDto -> {
                                                        ticketProfileDto.setEventId(CompositeID.parseId(eventProfile.getId()));
                                                        ticketProfileDto.setAvailableQuantity(ticketProfileDto.getQuantity());
                                                        return ticketProfileRepository.save(ticketProfileMapper.toTicketProfile(ticketProfileDto));
                                                    })
                                                    .then(Mono.just(eventProfile));
                                        }
                                        return Mono.just(eventProfile);
                                    }));
                }).thenReturn(eventProfileDto);
    }

    private Mono<EventProfileDto> validateEventProfile(EventProfileDto eventProfileDto) {
        logger.info("EventProfileService::validateEventProfile");
        if (eventProfileDto.getStartDate().compareTo(eventProfileDto.getEndDate()) > 0) {
            return Mono.error(new IllegalArgumentException("Start date cannot be after end date"));
        }
        return Mono.just(eventProfileDto);
    }

    @Transactional
    public Mono<Void> deleteEventProfile(String cId) {
        logger.info("EventProfileService::deleteEventProfile");
        return getEventProfileById(cId)
                .flatMap(eventProfileDto -> {
                    logger.info("Deleting event profile {}", eventProfileDto);
                    Long id = CompositeID.parseIdString(eventProfileDto.getId());
                    return ticketProfileRepository.deleteTicketProfilesByEventId(id)
                            .then(eventProfileRepository.deleteEventProfile(id));
                });
    }

    @Transactional
    public Mono<EventProfileDto> updateEventProfile(String id, Boolean ticketUpdated, EventProfileDto eventProfileDto) {
        logger.info("EventProfileService::updateEventProfile");
        return eventProfileRepository.getEventProfileById(CompositeID.parseIdString(id))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Event profile not found")))
                .flatMap(existingEventProfileWithCity -> {
                    EventProfile existingEventProfile = eventProfileMapper.toEventProfile(existingEventProfileWithCity);
                    return validateEventProfile(eventProfileDto)
                            .flatMap(eventProfileDto1 -> {
                                existingEventProfile.setName(eventProfileDto1.getName());
                                existingEventProfile.setStartDate(Instant.ofEpochSecond(Long.parseLong(eventProfileDto1.getStartDate())));
                                existingEventProfile.setEndDate(Instant.ofEpochSecond(Long.parseLong(eventProfileDto1.getEndDate())));
                                existingEventProfile.setLocation(eventProfileDto1.getLocation());
                                existingEventProfile.setCityId(CompositeID.parseIdString(eventProfileDto1.getCityId()));
                                existingEventProfile.setModifiedAt(Instant.now());
                                return eventProfileRepository.save(existingEventProfile);
                            });
                })
                .flatMap(eventProfile -> {
                    if (ticketUpdated != null && ticketUpdated) {
                        return ticketProfileRepository.getAllTicketProfilesByEventId(CompositeID.parseIdString(id))
                                .collectList()
                                .flatMap(allTicketProfiles -> {
                                    List<TicketProfileDto> ticketProfileDtos = eventProfileDto.getTickets();
                                    Map<String, List<TicketProfile>> ticketProfilesByOperation = getTicketProfilesByOperation(allTicketProfiles, ticketProfileDtos, CompositeID.parseId(eventProfile.getId()));
                                    List<TicketProfile> ticketProfilesToCreate = ticketProfilesByOperation.get(Constants.CREATE);
                                    List<TicketProfile> ticketProfilesToUpdate = ticketProfilesByOperation.get(Constants.UPDATE);
                                    List<TicketProfile> ticketProfilesToDelete = ticketProfilesByOperation.get(Constants.DELETE);
                                    return Flux.fromIterable(ticketProfilesToCreate)
                                            .flatMap(ticketProfile -> ticketProfileRepository.save(ticketProfile))
                                            .thenMany(Flux.fromIterable(ticketProfilesToUpdate)
                                                    .flatMap(ticketProfile -> ticketProfileRepository.save(ticketProfile)))
                                            .thenMany(Flux.fromIterable(ticketProfilesToDelete)
                                                    .flatMap(ticketProfile -> ticketProfileRepository.deleteTicketProfileById(ticketProfile.getId())))
                                            .then(Mono.just(eventProfile));
                                });
                    } else {
                        return Mono.just(eventProfile);
                    }
                }).thenReturn(eventProfileDto);
    }


    private Map<String, List<TicketProfile>> getTicketProfilesByOperation(List<TicketProfile> allExistingTicketProfiles, List<TicketProfileDto> ticketProfileDtos, String eventId) {
        logger.info("EventProfileService::getTicketProfilesByOperation");
        Map<String, List<TicketProfile>> ticketProfilesByOperation = new HashMap<>();

        List<TicketProfile> ticketProfilesToCreate = new ArrayList<>();
        List<TicketProfile> ticketProfilesToUpdate = new ArrayList<>();
        List<TicketProfile> ticketProfilesToDelete = new ArrayList<>();

        for (TicketProfileDto ticketProfileDto : ticketProfileDtos) {
            ticketProfileDto.setEventId(eventId);
            TicketProfile existingTicketProfile;
            if (ticketProfileDto.getId() == null) {
                existingTicketProfile = allExistingTicketProfiles.stream()
                        .filter(ticketProfile -> ticketProfile.getEventId().equals(CompositeID.parseIdString(ticketProfileDto.getEventId()))
                                && ticketProfile.getTicketType().equals(ticketProfileDto.getTicketType()))
                        .findFirst()
                        .orElse(null);
            } else {
                existingTicketProfile = allExistingTicketProfiles.stream()
                        .filter(ticketProfile -> ticketProfile.getId().equals(CompositeID.parseIdString(ticketProfileDto.getId())))
                        .findFirst()
                        .orElse(null);
            }
            if (existingTicketProfile == null) {
                ticketProfileDto.setAvailableQuantity(ticketProfileDto.getQuantity());
                ticketProfilesToCreate.add(ticketProfileMapper.toTicketProfile(ticketProfileDto));
            } else {
                ticketProfileDto.setId(CompositeID.parseId(existingTicketProfile.getId()));
                ticketProfileDto.setAvailableQuantity(existingTicketProfile.getAvailableQuantity());
                TicketProfile ticketProfile = ticketProfileMapper.toTicketProfile(ticketProfileDto);
                ticketProfile.setCreatedAt(Instant.now());
                ticketProfile.setModifiedAt(Instant.now());
                ticketProfile.setMiscflags(0L);
                ticketProfilesToUpdate.add(ticketProfile);
            }
        }

        for (TicketProfile existingTicketProfile : allExistingTicketProfiles) {
            if (ticketProfileDtos.stream().noneMatch(ticketProfileDto -> CompositeID.parseIdString(ticketProfileDto.getId()).equals(existingTicketProfile.getId()))) {
                ticketProfilesToDelete.add(existingTicketProfile);
            }
        }

        ticketProfilesByOperation.put(Constants.CREATE, ticketProfilesToCreate);
        ticketProfilesByOperation.put(Constants.UPDATE, ticketProfilesToUpdate);
        ticketProfilesByOperation.put(Constants.DELETE, ticketProfilesToDelete);
        return ticketProfilesByOperation;
    }
}
//@TODO: not null validations on eventprofile fields