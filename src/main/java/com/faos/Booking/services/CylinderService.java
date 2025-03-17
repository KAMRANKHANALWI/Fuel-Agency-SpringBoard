package com.faos.Booking.services;

import com.faos.Booking.exceptions.ResourceNotFoundException;
import com.faos.Booking.models.Cylinder;
import com.faos.Booking.models.Supplier;
import com.faos.Booking.models.enums.CylinderStatus;
import com.faos.Booking.repositories.CylinderRepository;
import com.faos.Booking.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CylinderService {

    @Autowired
    private CylinderRepository cylinderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    // ✅ Get all cylinders
    public List<Cylinder> getAllCylinders() {
        return cylinderRepository.findAll();
    }

    // ✅ Get cylinder by ID
    public Cylinder getCylinderById(int id) {
        return cylinderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cylinder not found with ID: " + id));
    }

    // ✅ Add cylinder with supplier
    @Transactional
    public Cylinder addCylinderWithSupplier(Cylinder cylinder, Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));

        cylinder.setSupplier(supplier);
        return cylinderRepository.save(cylinder);
    }

    // ✅ Update an existing cylinder
    @Transactional
    public Cylinder updateCylinder(int id, Cylinder updatedCylinder) {
        Cylinder cylinder = getCylinderById(id);
        cylinder.setCylinderType(updatedCylinder.getCylinderType());
        cylinder.setStatus(updatedCylinder.getStatus());
        cylinder.setLastRefillDate(updatedCylinder.getLastRefillDate());
        cylinder.setNextRefillDate(updatedCylinder.getNextRefillDate());
        return cylinderRepository.save(cylinder);
    }

    // ✅ Find Cylinder By Status
    public List<Cylinder> findByStatus(CylinderStatus status) {
        return cylinderRepository.findByStatus(status);
    }

    // ✅ Delete cylinder
    @Transactional
    public void deleteCylinder(int id) {
        if (!cylinderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cylinder with ID " + id + " not found.");
        }
        cylinderRepository.deleteById(id);
    }
}
