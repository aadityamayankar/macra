package com.mayankar.eventsync.service;

import com.mayankar.dataaccess.cachedrepository.TicketReservationRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicketReservationCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(TicketReservationCleanupService.class);

    @Autowired
    TicketReservationRepository ticketReservationRepository;
    @Autowired
    private TicketProfileRepository ticketProfileRepository;

    @Scheduled(fixedRateString = "${ibento.eventsync.ticket_reservation_cleanup_interval}", initialDelay = 0)
    public void cleanUpExpiredTicketReservations() {
        logger.info("Trying to clean up expired ticket reservations");

        ticketProfileRepository.getAllActiveTicketProfiles()
                .flatMap(ticketProfile -> {
                    String key = ticketReservationRepository.getTicketReservationKey(CompositeID.parseId(ticketProfile.getEventId()),
                            CompositeID.parseId(ticketProfile.getId()));
                    return ticketReservationRepository.cleanUpExpiredTicketReservations(key);
                })
                .collectList()
                .doOnSuccess(removedReservations -> {
                    long totalRemovedReservations = removedReservations.stream().mapToLong(Long::longValue).sum();
                    if (totalRemovedReservations > 0)
                        logger.info("Cleaned up {} expired ticket reservations", totalRemovedReservations);
                    else
                        logger.info("No expired ticket reservations to clean up");
                })
                .doOnError(throwable -> {
                    logger.error("Error cleaning up expired ticket reservations", throwable);
                })
                .subscribe();

    }
}
