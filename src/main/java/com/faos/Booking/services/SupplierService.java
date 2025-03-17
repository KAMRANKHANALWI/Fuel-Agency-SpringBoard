package com.faos.Booking.services;

import com.faos.Booking.exceptions.ResourceNotFoundException;
import com.faos.Booking.models.Supplier;
import com.faos.Booking.models.Cylinder;
import com.faos.Booking.models.enums.CylinderStatus;
import com.faos.Booking.models.enums.SupplierStatus;
import com.faos.Booking.repositories.SupplierRepository;
import com.faos.Booking.repositories.CylinderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private CylinderRepository cylinderRepository;

    // ✅ Get all suppliers
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    // ✅ Get supplier by ID
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
    }

    // ✅ Add a new supplier
    @Transactional
    public Supplier addSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    // ✅ Add more cylinders to an existing supplier
    @Transactional
    public Supplier addCylindersToSupplier(Long supplierId, int count) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));

        for (int i = 0; i < count; i++) {
            Cylinder cylinder = new Cylinder();
            cylinder.setSupplier(supplier);
            cylinder.setStatus(CylinderStatus.AVAILABLE);
            cylinderRepository.save(cylinder);
        }

        return supplier;
    }

    // ✅ Get total cylinders stock
    public int getTotalCylinderStock() {
        return (int) cylinderRepository.count();
    }

    // ✅ Get total cylinders stock of a supplier by ID
    public int getSupplierCylinderStock(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
        return supplier.getCylinderCount();
    }

    // ✅ Get suppliers by status ACTIVE/INACTIVE
    public List<Supplier> findByStatus(SupplierStatus status) {
        return supplierRepository.findBySupplierStatus(status);
    }

    // ✅ Get top supplier
    public List<Supplier> findTopSuppliers() {
        return supplierRepository.findTopSuppliers();
    }

    // ✅ Update an existing supplier
    @Transactional
    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        Supplier supplier = getSupplierById(id);
        supplier.setSupplierName(updatedSupplier.getSupplierName());
        supplier.setPhoneNo(updatedSupplier.getPhoneNo());
        supplier.setEmail(updatedSupplier.getEmail());
        supplier.setAddress(updatedSupplier.getAddress());
        supplier.setLicenseNumber(updatedSupplier.getLicenseNumber());
        supplier.setSupplierStatus(updatedSupplier.getSupplierStatus());
        return supplierRepository.save(supplier);
    }

    // ✅ Delete supplier
    @Transactional
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with ID: " + id);
        }
        supplierRepository.deleteById(id);
    }
}
