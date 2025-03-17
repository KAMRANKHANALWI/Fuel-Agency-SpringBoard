package com.faos.Booking.controllers;

import com.faos.Booking.models.Cylinder;
import com.faos.Booking.models.enums.CylinderStatus;
import com.faos.Booking.services.CylinderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cylinders")
public class CylinderController {

    @Autowired
    private CylinderService cylinderService;

    // ✅ Admin: Get all cylinders
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cylinder> getAllCylinders() {
        return cylinderService.getAllCylinders();
    }

    // ✅ Admin: Get cylinder by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cylinder> getCylinderById(@PathVariable int id) {
        return ResponseEntity.ok(cylinderService.getCylinderById(id));
    }

    // ✅ Admin: Add a new cylinder along with supplier
    @PostMapping("/add/{supplierId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cylinder> addCylinder(@PathVariable Long supplierId, @RequestBody Cylinder cylinder) {
        return ResponseEntity.ok(cylinderService.addCylinderWithSupplier(cylinder, supplierId));
    }

    // ✅ Admin: Update cylinder
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cylinder> updateCylinder(@PathVariable int id, @RequestBody Cylinder cylinder) {
        return ResponseEntity.ok(cylinderService.updateCylinder(id, cylinder));
    }

    // ✅ Customer/Admin: Find all available cylinders that can be booked
    @GetMapping("/available")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public List<Cylinder> getAvailableCylinders() {
        return cylinderService.findByStatus(CylinderStatus.AVAILABLE);
    }

    // ✅ Admin: Find all booked cylinders
    @GetMapping("/booked")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cylinder> getBookedCylinders() {
        return cylinderService.findByStatus(CylinderStatus.BOOKED);
    }

    // ✅ Admin: Delete cylinder
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCylinder(@PathVariable int id) {
        cylinderService.deleteCylinder(id);
        return ResponseEntity.noContent().build();
    }
}
