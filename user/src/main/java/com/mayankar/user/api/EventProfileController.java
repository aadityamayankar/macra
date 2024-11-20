package com.mayankar.user.api;

import com.mayankar.dto.EventProfileDto;
import com.mayankar.dto.EventsRequestDto;
import com.mayankar.user.service.EventProfileService;
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
public class EventProfileController {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileController.class);

    @Autowired
    EventProfileService eventProfileService;

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
                .deleted(false)
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

}
