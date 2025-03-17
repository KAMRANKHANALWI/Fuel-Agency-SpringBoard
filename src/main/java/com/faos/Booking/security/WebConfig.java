package com.faos.Booking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

//        // Allow only specific frontend URLs
//        configuration.setAllowedOrigins(List.of("http://localhost:1000", "https://frontend.example.com"));

        // Or, use patterns for more flexibility
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.example.com"));

        // Allowed HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allowed headers
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Cookie"));

        // Expose headers to frontend
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        // Allow credentials (for session authentication)
        configuration.setAllowCredentials(true);

        // Apply configuration to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}