package com.mayankar.opsadmin.api;

import com.mayankar.controller.BaseController;
import com.mayankar.opsadmin.dto.EventProfileDto;
import com.mayankar.opsadmin.service.EventProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/events")
public class EventProfileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileController.class);

    @Autowired
    private EventProfileService eventProfileService;

    @GetMapping
    public Flux<EventProfileDto> getAllEvents(ServerWebExchange exchange) {//@TODO: add filters (city, ...etc)
        logger.debug("EventProfileController::getAllEvents");
        return eventProfileService.getAllEventProfiles()
                .doOnComplete(() -> logger.info("All events fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching all events"));
    }

    @GetMapping("/{id}")
    public Mono<EventProfileDto> getEventById(ServerWebExchange exchange, @PathVariable(value = "id") String id) {
        logger.debug("EventProfileController::getEventById {}", id);
        return eventProfileService.getEventProfileById(id);
    }

    @PostMapping
    public Mono<EventProfileDto> createEventProfile(ServerWebExchange exchange, @RequestBody EventProfileDto eventProfileDto) {
        logger.debug("EventProfileController::createEventProfile {}", eventProfileDto);
        return eventProfileService.createEventProfile(eventProfileDto)
                .doOnSuccess(eventProfile -> logger.info("Event {} created successfully", eventProfile.getName()))
                .doOnError(throwable -> logger.error("Error creating event {}", eventProfileDto.getName()));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteEventProfile(ServerWebExchange exchange, @PathVariable(value = "id") String id) {
        logger.debug("EventProfileController::deleteEventProfile {}", id);
        return eventProfileService.deleteEventProfile(id)
                .doOnSuccess(aVoid -> logger.info("Event {} deleted successfully", id))
                .doOnError(throwable -> logger.error("Error deleting event {}", id));
    }
}
