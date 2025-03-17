package com.faos.Booking.controllers;

import com.faos.Booking.models.Customer;
import com.faos.Booking.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ✅ Admin: Get all customers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    // ✅ Customer/Admin: Get customer by ID (Customers can only fetch their own profile)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id, Principal principal) {
        Customer customer = customerService.getCustomerById(id);
        if (!principal.getName().equals(customer.getUsername()) && !customer.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(customer);
    }

    // View own profile
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Customer> getMyProfile() {
        Customer customer = customerService.getCurrentCustomer();
        return ResponseEntity.ok(customer);
    }

    // ✅ Public: Register a new customer (Default role CUSTOMER)
    @PostMapping("/register")
    public ResponseEntity<Customer> addCustomer(@Valid @RequestBody Customer customer) {
        customer.setRole(com.faos.Booking.models.enums.UserRole.CUSTOMER);
        Customer saved = customerService.addCustomer(customer);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ Customer/Admin: Update own profile or Admin updates anyone
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @Valid @RequestBody Customer updatedCustomer, Principal principal) {
        Customer customer = customerService.getCustomerById(id);
        if (!principal.getName().equals(customer.getUsername()) && !customer.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Customer updated = customerService.updateCustomer(id, updatedCustomer);
        return ResponseEntity.ok(updated);
    }

    // ✅ Admin: Delete a customer
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Customer deleted successfully!");
    }
}
