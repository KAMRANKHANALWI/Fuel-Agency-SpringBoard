//package com.faos.Booking.models;
//
//import com.faos.Booking.models.enums.ConnectionStatus;
//import com.faos.Booking.models.enums.UserRole;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.time.LocalDate;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "customers")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bookings"})
//public class Customer implements UserDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long customerId;
//
//    @NotBlank(message = "Username cannot be blank.")
//    private String username;
//
//    private String password;
//
//    @NotBlank(message = "First name cannot be blank.")
//    private String firstName;
//
//    @NotBlank(message = "Last name cannot be blank.")
//    private String lastName;
//
//    @Email(message = "Email should be valid.")
//    @NotBlank(message = "Email cannot be blank.")
//    private String email;
//
//    private String phone;
//
//    private String address;
//
//    private LocalDate registrationDate;
//
//    @Enumerated(EnumType.STRING)
//    @NotNull(message = "Connection status cannot be null.")
//    private ConnectionStatus connectionStatus = ConnectionStatus.ACTIVE;
//
//    @Enumerated(EnumType.STRING)
//    private UserRole role = UserRole.CUSTOMER;  // Default role
//
//    public void setPassword(String rawPassword) {
//        this.password = rawPassword;
//    }
//
//    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonIgnoreProperties(value = {"customer"})
//    private List<Booking> bookings;
//
//    // ✅ Returns roles with 'ROLE_' prefix
//    public String getAuthorityRole() {
//        return "ROLE_" + this.role.name();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority(getAuthorityRole()));
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

package com.faos.Booking.models;

import com.faos.Booking.models.enums.ConnectionStatus;
import com.faos.Booking.models.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bookings"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @NotBlank(message = "Username cannot be blank.")
    private String username;

    private String password;

    @NotBlank(message = "First name cannot be blank.")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank.")
    private String lastName;

    @Email(message = "Email should be valid.")
    @NotBlank(message = "Email cannot be blank.")
    private String email;

    private String phone;
    private String address;
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Connection status cannot be null.")
    private ConnectionStatus connectionStatus = ConnectionStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.CUSTOMER;  // Default role

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"customer"})
    private List<Booking> bookings;

}

