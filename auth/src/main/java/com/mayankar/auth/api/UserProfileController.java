package com.mayankar.auth.api;

import com.mayankar.dataaccess.repository.UserProfileRepository;
import com.mayankar.model.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/users")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @GetMapping("/name")
    public Flux<UserProfile> getByName(@RequestParam String name) {
        return userProfileRepository.findByName(name);
    }

    @GetMapping()
    public Mono<String> hello() {
        return Mono.just("Hello");
    }
}
