package com.mayankar.user.service;

import com.mayankar.dataaccess.repository.CityProfileRepository;
import com.mayankar.dto.CityProfileDto;
import com.mayankar.mapper.CityProfileMapper;
import com.mayankar.util.CompositeID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CityProfileService {
    private static final Logger logger = LoggerFactory.getLogger(CityProfileService.class);
    @Autowired
    CityProfileRepository cityProfileRepository;
    @Autowired
    private CityProfileMapper cityProfileMapper;

    public Flux<CityProfileDto> getAllCities(ServerWebExchange exchange) {
        logger.info("CityProfileService::getAllCities");
        return cityProfileRepository.getAllCities()
                .map(cityProfileMapper::toCityProfileDto)
                .doOnComplete(() -> logger.info("All cities fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching all cities"));
    }

    public Mono<CityProfileDto> getCityProfileById(ServerWebExchange exchange, String id) {
        logger.info("CityProfileService::getCityProfileById");
        return cityProfileRepository.getCityProfileById(CompositeID.parseIdString(id))
                .map(cityProfileMapper::toCityProfileDto)
                .doOnSuccess(cityProfileDto -> logger.info("City fetched successfully"))
                .doOnError(throwable -> logger.error("Error fetching city"));
    }
}
