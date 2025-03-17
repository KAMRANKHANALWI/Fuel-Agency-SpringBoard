package com.faos.Booking.repositories;

import com.faos.Booking.models.Supplier;
import com.faos.Booking.models.enums.SupplierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findBySupplierStatus(SupplierStatus status);

    @Query("SELECT s FROM Supplier s JOIN s.cylinders c GROUP BY s ORDER BY COUNT(c) DESC")
    List<Supplier> findTopSuppliers();
}