package com.kiyotakeshi.basicauthentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/greeting")
public class GreetingController {

    @GetMapping
    public String greeting() {
        return "hello, there";
    }
}
