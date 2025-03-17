package com.faos.Booking.controllers;

import com.faos.Booking.models.Bill;
import com.faos.Booking.models.Booking;
import org.springframework.http.HttpHeaders;
import com.faos.Booking.services.BillService;
import com.faos.Booking.services.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private EmailSenderService emailSenderService;

    // ✅ Admin: Send bill email
    @PostMapping("/{billId}/send-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> sendBillEmail(@PathVariable Long billId) {
        Bill bill = billService.getBillById(billId);
        if (bill == null) {
            return ResponseEntity.notFound().build();
        }
        emailSenderService.sendEmailWithAttachment(bill);
        return ResponseEntity.ok("Bill sent to " + bill.getBooking().getCustomer().getEmail());
    }

    // ✅ Admin: Get all bills
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Bill>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    // ✅ Customer/Admin: Get bill by ID (Customers can only view their own bills)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id, Principal principal) {
        Bill bill = billService.getBillById(id);

        String currentUsername = principal.getName();
        boolean isOwner = currentUsername.equals(bill.getBooking().getCustomer().getUsername());
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuth -> grantedAuth.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(bill);
    }

    // ✅ Admin: Update bill
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody Bill updatedBill) {
        Bill savedBill = billService.updateBill(id, updatedBill);
        return ResponseEntity.ok(savedBill);
    }

    // ✅ Admin: Delete bill
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.ok("Bill deleted successfully!");
    }
}