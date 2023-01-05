package com.java8.tms.user.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class HomePageController {

    @PreAuthorize("hasAuthority('VIEW_CLASS')")
    @GetMapping("/user")
    public String helloUser() {
        return "Hello User";
    }

    @PreAuthorize("hasAuthority('FULL_ACCESS_CLASS')")
    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello Admin";
    }
}
