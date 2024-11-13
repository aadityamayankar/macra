package com.mayankar.opsadmin.api;

import com.mayankar.model.EventProfile;
import com.mayankar.opsadmin.dto.EventProfileDto;
import com.mayankar.opsadmin.service.EventProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/events")
public class EventProfileController {
    private static final Logger logger = LoggerFactory.getLogger(EventProfileController.class);
    private final EventProfileService eventProfileService;

    public EventProfileController(EventProfileService eventProfileService) {
        this.eventProfileService = eventProfileService;
    }

    @GetMapping
    public Flux<EventProfile> getAllEvents(WebSession session) {
        return eventProfileService.getAllEventProfiles();
    }

    @GetMapping("/{id}")
    public Mono<EventProfile> getEventById(@RequestParam(value = "id") String id, WebSession session) {
        return eventProfileService.getEventProfileById(id);
    }

    @PostMapping
    public Mono<EventProfileDto> createEventProfile(@RequestBody EventProfileDto eventProfileDto, WebSession session) {
        return eventProfileService.createEventProfile(eventProfileDto);
    }

//    @DeleteMapping("/{id}")
//    public Mono<Void> deleteEventProfile(@RequestParam(value = "id") String id, WebSession session) {
//        return eventProfileService.deleteEventProfile(id);
//    }
}
