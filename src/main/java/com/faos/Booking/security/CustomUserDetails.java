package com.faos.Booking.security;

import com.faos.Booking.models.Customer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Customer customer;

    public CustomUserDetails(Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole().name()));
    }

    @Override
    public String getPassword() {
        return customer.getPassword();
    }

    @Override
    public String getUsername() {
        return customer.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Modify if implementing expiration logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Modify if implementing lock logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Modify if implementing credential expiration
    }

    @Override
    public boolean isEnabled() {
        return true; // Modify if implementing user enable/disable logic
    }
}

