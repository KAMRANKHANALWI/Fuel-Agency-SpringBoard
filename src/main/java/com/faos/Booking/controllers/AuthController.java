package com.faos.Booking.controllers;

import com.faos.Booking.models.Customer;
import com.faos.Booking.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/whoami")
    public ResponseEntity<?> whoAmI() {
        return authService.whoAmI();
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getAuthenticatedUser(HttpServletRequest request) {
        return authService.getAuthenticatedUser(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Customer customer) {
        return authService.register(customer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest, HttpSession session) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        return authService.login(username, password, session);
    }

//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpSession session) {
//        return authService.logout(session);
//    }
}