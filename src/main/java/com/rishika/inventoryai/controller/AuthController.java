package com.rishika.inventoryai.controller;

import com.rishika.inventoryai.auth.LoginRequest;
import com.rishika.inventoryai.model.User;
import com.rishika.inventoryai.repository.UserRepository;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repository;

    public AuthController(UserRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/login")
    public String login(
            @RequestBody LoginRequest request) {

        User user =
                repository.findByUsername(
                                request.getUsername())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        if(user.getPassword()
                .equals(request.getPassword())) {

            return "LOGIN_SUCCESS";
        }

        return "INVALID_PASSWORD";
    }
}