package com.mayankar.dataaccess.cachedrepository;

import com.mayankar.dataaccess.service.ReactiveRedisService;
import com.mayankar.model.TicketProfile;
import com.mayankar.util.CompositeID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.util.CacheConstants.TICKET_PROFILE_PREFIX;

@Repository
public class TicketProfileCacheRepository {
    @Autowired
    ReactiveRedisService<TicketProfile> reactiveRedisService;

    public Mono<TicketProfile> saveTicketProfile(TicketProfile ticketProfile) {
        return reactiveRedisService.persistentSave(TICKET_PROFILE_PREFIX, CompositeID.parseId(ticketProfile.getId()), ticketProfile);
    }

    public Mono<TicketProfile> getTicketProfile(String id) {
        return reactiveRedisService.get(TICKET_PROFILE_PREFIX, id, TicketProfile.class);
    }

    public Flux<TicketProfile> getTicketProfilesByEventId(Long eventId) {
        return reactiveRedisService.search(TICKET_PROFILE_PREFIX, "eventId", eventId, TicketProfile.class);
    }
}
