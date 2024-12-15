package com.mayankar.user.api;

import com.mayankar.dto.CityProfileDto;
import com.mayankar.user.service.CityProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.mayankar.controller.BaseController.API_V1;

@RestController
@RequestMapping(API_V1 + "/cities")
public class CityProfileController {
    private static final Logger logger = LoggerFactory.getLogger(CityProfileController.class);

    @Autowired
    CityProfileService cityProfileService;

    @GetMapping
    public Flux<CityProfileDto> getAllCities(ServerWebExchange exchange) {
        return cityProfileService.getAllCities(exchange);
    }

    @GetMapping("/{id}")
    public Mono<CityProfileDto> getCityById(ServerWebExchange exchange, @PathVariable(value = "id") String id) {
        return cityProfileService.getCityProfileById(exchange, id);
    }
}
