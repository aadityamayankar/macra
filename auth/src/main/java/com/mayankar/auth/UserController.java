package com.mayankar.auth;

import com.mayankar.dataaccess.repository.UserRepository;
import com.mayankar.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/name")
    public Flux<User> getByName(@RequestParam String name) {
        return userRepository.findByName(name);
    }
}
