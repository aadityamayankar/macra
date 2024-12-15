package com.mayankar.user.service;

import com.mayankar.dataaccess.cachedrepository.AuthnSessionRepository;
import com.mayankar.dto.SessionResponseDto;
import com.mayankar.model.AuthnSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AuthnService {
    private static final Logger logger = LoggerFactory.getLogger(AuthnService.class);

    @Autowired
    AuthnSessionRepository authnSessionRepository;

    public Mono<SessionResponseDto> getSessionStatus(AuthnSession authnSession) {
        String id = authnSession.getId();
        String userId = authnSession.getUserId();
        logger.debug("Retrieving session status for session: {}, user: {}", id, userId);
        SessionResponseDto sessionResponseDto = new SessionResponseDto();
        return authnSessionRepository.getSession(id)
                .flatMap(authnSession1 -> {
                    if (authnSession1.getUserId().equals(userId)) {
                        sessionResponseDto.setIsAuthenticated(true);
                        return Mono.just(sessionResponseDto);
                    } else {
                        logger.error("Invalid session for user: {}", userId);
                        return Mono.error(new RuntimeException("Invalid session"));
                    }
                })
                .switchIfEmpty(Mono.just(sessionResponseDto));
    }
}
