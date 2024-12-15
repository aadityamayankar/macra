package com.mayankar.opsadmin.api;

import com.mayankar.controller.BaseController;
import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventsRequestDto;
import com.mayankar.opsadmin.service.EventProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/events")
public class EventProfileController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileController.class);

    @Autowired
    private EventProfileService eventProfileService;

    //@TODO: pagination
    @GetMapping
    public Flux<EventProfileDto> getAllEvents(ServerWebExchange exchange,
                                              @RequestParam(value = "city", required = false) String city,
                                              @RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "startDate", required = false) String startDate,
                                              @RequestParam(value = "endDate", required = false) String endDate,
                                              @RequestParam(value = "deleted", required = false, defaultValue = "false") Boolean deleted
    ) {
        logger.debug("EventProfileController::getAllEvents city={} name={} startDate={} endDate={} deleted={}", city, name, startDate, endDate, deleted);
        EventsRequestDto eventsRequestDto = EventsRequestDto.builder()
                .city(city)
                .name(name)
                .startDate(startDate)
                .endDate(endDate)
                .deleted(deleted)
                .build();
        return eventProfileService.getAllEventProfiles(eventsRequestDto)
                .doOnComplete(() -> logger.info("All events fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching all events"));
    }

    @GetMapping("/{id}")
    public Mono<EventProfileDto> getEventById(ServerWebExchange exchange, @PathVariable(value = "id") String id) {
        logger.debug("EventProfileController::getEventById {}", id);
        return eventProfileService.getEventProfileById(id)
                .doOnSuccess(eventProfileDto -> logger.info("Event fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching event"));
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

    @PutMapping("/{id}")
    public Mono<EventProfileDto> updateEventProfile(ServerWebExchange exchange, @PathVariable(value = "id") String id, @RequestParam(value = "ticket_updated", required = false, defaultValue = "false") Boolean ticketUpdated, @RequestBody EventProfileDto eventProfileDto) {
        logger.debug("EventProfileController::updateEventProfile {}", id);
        return eventProfileService.updateEventProfile(id, ticketUpdated, eventProfileDto)
                .doOnSuccess(eventProfile -> logger.info("Event {} updated successfully", eventProfile.getName()))
                .doOnError(throwable -> logger.error("Error updating event {}", id));
    }
}
