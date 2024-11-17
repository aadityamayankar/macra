package com.mayankar.user.api;

import com.mayankar.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/test")
public class TestController extends BaseController {
    @GetMapping
    public String test() {
        return "Hello World!";
    }
}
