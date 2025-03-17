
package com.faos.Booking.strategies.impl;

import com.faos.Booking.models.Supplier;
import com.faos.Booking.strategies.SupplierSelectionStrategy;

import java.util.Comparator;
import java.util.List;

public class LeastStockStrategy implements SupplierSelectionStrategy {
    @Override
    public Supplier selectSupplier(List<Supplier> suppliers) {
        return suppliers.stream()
                .filter(supplier -> supplier.getAvailableCylinderCount() > 0)
                .min(Comparator.comparingInt(Supplier::getAvailableCylinderCount))
                .orElseThrow(() -> new RuntimeException("No cylinder is available to booked! Please contact your supplier"));
    }
}
