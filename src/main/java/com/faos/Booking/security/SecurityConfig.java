package com.faos.Booking.security;

import com.faos.Booking.repositories.CustomerRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Optional;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource)) // Enable CORS for frontend
                .csrf(csrf -> csrf.disable()) // Disable CSRF for API endpoints
                .securityContext(context -> context.requireExplicitSave(false)) // Keep authentication session
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/auth/login", "/auth/register", "/auth/logout", "/auth/whoami").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/customer/**").hasAuthority("ROLE_CUSTOMER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public UserDetailsService userDetailsService(CustomerRepository customerRepository) {
        return username -> {
            Optional<com.faos.Booking.models.Customer> optionalCustomer = customerRepository.findByUsername(username);
            com.faos.Booking.models.Customer customer = optionalCustomer
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return new CustomUserDetails(customer);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


//package com.faos.Booking.security;
//
//import com.faos.Booking.repositories.CustomerRepository;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.ProviderManager;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import java.util.Optional;
//
//@Configuration
//@EnableMethodSecurity  // This enables method-level security
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
//            throws Exception {
//        return http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource))
//                .csrf(csrf -> csrf.disable())
//                .securityContext(context -> context.requireExplicitSave(false))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/auth/login",
//                                "/auth/register",
//                                "/auth/logout",
//                                "/auth/whoami",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/css/**", "/js/**", "/images/**"
//                        ).permitAll()
//                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
//                        .requestMatchers("/customer/**").hasAuthority("ROLE_CUSTOMER")
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
//                .logout(logout -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
//                        .logoutSuccessHandler((request, response, authentication) -> {
//                            response.setStatus(HttpServletResponse.SC_OK);
//                            response.setContentType("application/json");
//                            response.getWriter().write("{\"message\": \"Logout successful\"}");
//                        })
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
//                .build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder);
//        return new ProviderManager(authProvider);
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(CustomerRepository customerRepository) {
//        return username -> {
//            Optional<com.faos.Booking.models.Customer> optionalCustomer = customerRepository.findByUsername(username);
//            com.faos.Booking.models.Customer customer = optionalCustomer
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//            return new CustomUserDetails(customer);
//        };
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}