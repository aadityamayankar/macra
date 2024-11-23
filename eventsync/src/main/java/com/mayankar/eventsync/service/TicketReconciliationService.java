package com.mayankar.eventsync.service;

import com.mayankar.dataaccess.cachedrepository.LastSyncTimeRepository;
import com.mayankar.dataaccess.cachedrepository.TicketProfileCacheRepository;
import com.mayankar.dataaccess.repository.TicketProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static com.mayankar.util.Constants.TICKET;

@Service
public class TicketReconciliationService {
    private static final Logger logger = LoggerFactory.getLogger(TicketReconciliationService.class);

    @Autowired
    TicketProfileRepository ticketProfileRepository;

    @Autowired
    LastSyncTimeRepository lastSyncTimeRepository;

    @Autowired
    TicketProfileCacheRepository ticketProfileCacheRepository;

    @Scheduled(fixedRateString = "${ibento.eventsync.ticket_reconciliation_interval}", initialDelay = 0)
    public void reconcileTickets() {
        logger.info("Trying to reconciling tickets...");

        lastSyncTimeRepository.getLastSyncTime(TICKET)
                .flatMap(lastSyncTicketTime -> ticketProfileRepository.getAllTicketProfilesUpdatedAfter(lastSyncTicketTime)
                        .flatMap(ticketProfile -> ticketProfileCacheRepository.saveTicketProfile(ticketProfile))
                        .collectList()
                        .map(List::size)).zipWhen(ticketsReconciled -> Mono.just(Instant.now()))
                .flatMap(tuple2 -> lastSyncTimeRepository.saveLastSyncTime(TICKET, tuple2.getT2())
                        .map(lastSyncTime -> tuple2.getT1()))
                .doOnSuccess(ticketsReconciled -> {
                    if (ticketsReconciled > 0)
                        logger.info("Reconciled {} tickets", ticketsReconciled);
                    else
                        logger.info("No tickets to reconcile");
                })
                .doOnError(throwable -> {
                    logger.error("Error reconciling tickets", throwable);
                })
                .subscribe();
    }
}
