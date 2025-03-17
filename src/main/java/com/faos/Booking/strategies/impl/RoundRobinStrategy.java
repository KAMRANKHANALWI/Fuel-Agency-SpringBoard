package com.faos.Booking.strategies.impl;

import com.faos.Booking.models.Supplier;
import com.faos.Booking.strategies.SupplierSelectionStrategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements SupplierSelectionStrategy {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public Supplier selectSupplier(List<Supplier> suppliers) {
        // Filter the list to only include suppliers with available cylinders.
        List<Supplier> availableSuppliers = suppliers.stream()
                .filter(supplier -> supplier.getAvailableCylinderCount() > 0)
                .toList();
        if (availableSuppliers.isEmpty()) {
            throw new RuntimeException("No cylinder is available to booked! Please contact your supplier");
        }
        int supplierIndex = index.getAndIncrement() % availableSuppliers.size();
        return availableSuppliers.get(supplierIndex);
    }
}
