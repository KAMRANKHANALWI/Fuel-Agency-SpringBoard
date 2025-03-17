package com.faos.Booking.repositories;

import com.faos.Booking.models.Cylinder;
import com.faos.Booking.models.Supplier;
import com.faos.Booking.models.enums.CylinderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface CylinderRepository extends JpaRepository<Cylinder, Integer> {

    Optional<Cylinder> findTopBySupplierAndStatus(Supplier supplier, CylinderStatus status);

    List<Cylinder> findByStatus(CylinderStatus status);
}
