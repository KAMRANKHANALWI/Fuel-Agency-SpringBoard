package com.faos.Booking.controllers;

import com.faos.Booking.models.Booking;
import com.faos.Booking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // ✅ Admin: Get all bookings
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBooking());
    }

    // ✅ Customer/Admin: Get booking by ID (Customers can only access their own bookings)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // ✅ Customer: Get all booking of current logged-in user
    @GetMapping("/mybookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<Booking>> getMyBookings() {
        List<Booking> bookings = bookingService.getBookingsForCurrentCustomer();
        return ResponseEntity.ok(bookings);
    }

    // ✅ Customer: Create a new booking (Only customers can book)
    @PostMapping("/customer/booking")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Booking> addBooking(@Valid @RequestBody Booking booking) {
        Booking created = bookingService.addBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ✅ Customer/Admin: Update booking (Customers can only modify their own bookings)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @Valid @RequestBody Booking updatedBooking) {
        Booking updated = bookingService.updateBooking(id, updatedBooking);
        return ResponseEntity.ok(updated);
    }

    // ✅ Customer/Admin: Cancel booking (Customers can only cancel their own bookings)
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Booking> cancelBooking(@PathVariable Long id) {
        Booking cancelled = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelled);
    }

    // ✅ Admin: Delete booking
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok("Booking deleted successfully!");
    }

    // ✅ Admin: Get all cancelled bookings
    @GetMapping("/cancelled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Booking>> getAllCancelledBookings() {
        return ResponseEntity.ok(bookingService.getAllCancelledBookings());
    }
}
