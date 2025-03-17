package com.faos.Booking.strategies;

import com.faos.Booking.models.Supplier;

import java.util.List;

public interface SupplierSelectionStrategy {
    Supplier selectSupplier(List<Supplier> suppliers);
}

