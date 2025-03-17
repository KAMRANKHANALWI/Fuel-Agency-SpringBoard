package com.faos.Booking.controllers;

import com.faos.Booking.models.Supplier;
import com.faos.Booking.models.enums.SupplierStatus;
import com.faos.Booking.services.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    // ✅ Admin: Get all suppliers
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Supplier> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    // ✅ Admin: Get supplier by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Supplier> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    // ✅ Admin: Add a new supplier
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Supplier> addSupplier(@RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.addSupplier(supplier));
    }

    // ✅ Admin: Add more cylinders to a supplier
    @PostMapping("/{supplierId}/add-cylinders/{count}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Supplier> addMoreCylinders(@PathVariable Long supplierId, @PathVariable int count) {
        return ResponseEntity.ok(supplierService.addCylindersToSupplier(supplierId, count));
    }

    // ✅ Admin: Get total cylinder stock
    @GetMapping("/total-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> getTotalCylinderStock() {
        return ResponseEntity.ok(supplierService.getTotalCylinderStock());
    }

    // ✅ Customer/Admin: View active suppliers
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Supplier> getActiveSuppliers() {
        return supplierService.findByStatus(SupplierStatus.ACTIVE);
    }

    // ✅ Admin: View inactive suppliers
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Supplier> getInactiveSuppliers() {
        return supplierService.findByStatus(SupplierStatus.INACTIVE);
    }

    // ✅ Admin: Get top suppliers by cylinder count
    @GetMapping("/top-suppliers")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Supplier> getTopSuppliers() {
        return supplierService.findTopSuppliers();
    }

    // ✅ Admin: Update supplier
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Supplier> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplier));
    }

    // ✅ Admin: Get total stock of a particular supplier
    @GetMapping("/{supplierId}/stock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> getSupplierCylinderStock(@PathVariable Long supplierId) {
        return ResponseEntity.ok(supplierService.getSupplierCylinderStock(supplierId));
    }

    // ✅ Admin: Delete supplier
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}