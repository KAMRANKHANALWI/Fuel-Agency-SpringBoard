package com.faos.Booking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bills")
@JsonPropertyOrder({"billId", "price", "gst", "deliveryCharge", "CLECharge", "totalPrice"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "booking"})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billId;

    @NotNull
    @DecimalMin(value = "0.0", message = "Price must be >= 0.")
    private BigDecimal price = BigDecimal.valueOf(1000);

    @DecimalMin(value = "0.0", message = "GST must be >= 0.")
    private BigDecimal gst;

    @DecimalMin(value = "0.0", message = "Delivery charge must be >= 0.")
    private BigDecimal deliveryCharge;

    @Column(name = "CLE_charge")
    @DecimalMin(value = "0.0", message = "CLE charge must be >= 0.")
    private BigDecimal CLECharge = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "Total price must be >= 0.")
    private BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    @JsonIgnoreProperties(value = {"bill"})
    private Booking booking;

    // Example calculation
    public void calculateTotalPrice(int cylinderCount, boolean exceededLimit) {
        BigDecimal calculatedTotal = price.add(gst != null ? gst : BigDecimal.ZERO)
                .add(deliveryCharge != null ? deliveryCharge : BigDecimal.ZERO);

        if (exceededLimit) {
            CLECharge = calculatedTotal.multiply(BigDecimal.valueOf(0.20));
            calculatedTotal = calculatedTotal.add(CLECharge);
        }

        this.totalPrice = calculatedTotal;
    }
}

