//package com.faos.Booking.services;
//
//import com.faos.Booking.exceptions.ResourceNotFoundException;
//import com.faos.Booking.models.Customer;
//import com.faos.Booking.repositories.CustomerRepository;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import java.util.List;
//
//@Service
//public class CustomerService {
//
//    private final CustomerRepository customerRepository;
//
//    public CustomerService(CustomerRepository customerRepository) {
//        this.customerRepository = customerRepository;
//    }
//
//    // ✅ Admin: Get all customers
//    public List<Customer> getAllCustomers() {
//        return customerRepository.findAll();
//    }
//
//    // ✅ Customer/Admin: Get customer by ID (Customers can only fetch their own profile)
//    public Customer getCustomerById(Long id) {
//        Customer customer = customerRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
//        ensureCustomerOrAdminAccess(customer);
//        return customer;
//    }
//
//    // ✅ Customer/Admin: Update own profile or Admin updates anyone
//    @Transactional
//    public Customer updateCustomer(Long id, Customer updatedCustomer) {
//        Customer customer = getCustomerById(id);
//        ensureCustomerOrAdminAccess(customer);
//        customer.setFirstName(updatedCustomer.getFirstName());
//        customer.setLastName(updatedCustomer.getLastName());
//        customer.setEmail(updatedCustomer.getEmail());
//        return customerRepository.save(customer);
//    }
//
//    // ✅ Admin: Delete customer
//    @Transactional
//    public void deleteCustomer(Long id) {
//        if (!customerRepository.existsById(id)) {
//            throw new ResourceNotFoundException("Customer not found with ID: " + id);
//        }
//        customerRepository.deleteById(id);
//    }
//
//    // ✅ Fetch the currently logged-in customer
//    public Customer getCurrentCustomer() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return customerRepository.findByUsername(username)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//    }
//
//    // ✅ Ensure access control
//    private void ensureCustomerOrAdminAccess(Customer customer) {
//        Customer currentUser = getCurrentCustomer();
//        if (!currentUser.getUsername().equals(customer.getUsername()) && !currentUser.getRole().name().equals("ADMIN")) {
//            throw new RuntimeException("Access Denied: You are not authorized to perform this action");
//        }
//    }
//}


package com.faos.Booking.services;

import com.faos.Booking.exceptions.ResourceNotFoundException;
import com.faos.Booking.models.Customer;
import com.faos.Booking.models.enums.UserRole;
import com.faos.Booking.repositories.CustomerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ✅ Admin: Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // ✅ Customer/Admin: Get customer by ID (Customers can only fetch their own profile)
    public Customer getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + id));
        ensureCustomerOrAdminAccess(customer);
        return customer;
    }

    // ✅ Public: Register a new customer (Default role CUSTOMER)
    @Transactional
    public Customer addCustomer(Customer customer) {
        customer.setRole(UserRole.CUSTOMER);
        return customerRepository.save(customer);
    }


    // ✅ Customer/Admin: Update own profile or Admin updates anyone
    @Transactional
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer customer = getCustomerById(id);
        ensureCustomerOrAdminAccess(customer);
        customer.setFirstName(updatedCustomer.getFirstName());
        customer.setLastName(updatedCustomer.getLastName());
        customer.setEmail(updatedCustomer.getEmail());
        return customerRepository.save(customer);
    }

    // ✅ Admin: Delete customer
    @Transactional
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    // ✅ Fetch the currently logged-in customer
    public Customer getCurrentCustomer() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // ✅ Ensure access control
    private void ensureCustomerOrAdminAccess(Customer customer) {
        Customer currentUser = getCurrentCustomer();
        if (!currentUser.getUsername().equals(customer.getUsername()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access Denied: You are not authorized to perform this action");
        }
    }
}
