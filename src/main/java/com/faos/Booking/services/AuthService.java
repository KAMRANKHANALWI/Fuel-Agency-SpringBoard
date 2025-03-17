package com.faos.Booking.services;

import com.faos.Booking.models.Customer;
import com.faos.Booking.repositories.CustomerRepository;
import com.faos.Booking.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> register(Customer customer) {
        Optional<Customer> existingUser = customerRepository.findByUsername(customer.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Username already exists"));
        }

        // Hash the password before saving
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "user", Map.of("username", customer.getUsername(), "role", customer.getRole())
        ));
    }

    public ResponseEntity<?> login(String username, String password, HttpSession session) {
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(authRequest);
            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Optionally store custom user details in the session
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                session.setAttribute("USER", principal);
            }
            return ResponseEntity.ok(Map.of(
                    "message", "Login successful",
                    "user", Map.of("username", username)
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }

    public ResponseEntity<?> whoAmI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() &&
                !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"))) {
            return ResponseEntity.ok(Map.of("user", auth.getName()));
        }
        return ResponseEntity.ok(Map.of("user", "anonymous"));
    }

    public ResponseEntity<Map<String, Object>> getAuthenticatedUser(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not authenticated"));
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Extract roles
        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Construct response
        Map<String, Object> response = new HashMap<>();
        response.put("username", userDetails.getUsername());
        response.put("roles", roles);
        response.put("sessionId", request.getSession().getId());

        return ResponseEntity.ok(response);
    }
}