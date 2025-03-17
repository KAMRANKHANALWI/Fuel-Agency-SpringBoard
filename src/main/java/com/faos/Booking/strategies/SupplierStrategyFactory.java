package com.faos.Booking.strategies;

import com.faos.Booking.strategies.impl.LeastStockStrategy;
import com.faos.Booking.strategies.impl.MostStockStrategy;
import com.faos.Booking.strategies.impl.RoundRobinStrategy;

public class SupplierStrategyFactory {
    // Use a single instance of RoundRobinStrategy so its counter is maintained across bookings.
    private static final RoundRobinStrategy ROUND_ROBIN_STRATEGY = new RoundRobinStrategy();

    public static SupplierSelectionStrategy getStrategy(String strategyType) {
        if (strategyType == null) {
            throw new IllegalArgumentException("Strategy type cannot be null");
        }
        return switch (strategyType.toUpperCase()) {
            case "MOST_STOCK" -> new MostStockStrategy();
            case "LEAST_STOCK" -> new LeastStockStrategy();
            case "ROUND_ROBIN" -> ROUND_ROBIN_STRATEGY;
            default -> throw new IllegalArgumentException("Invalid supplier selection strategy: " + strategyType);
        };
    }
}
