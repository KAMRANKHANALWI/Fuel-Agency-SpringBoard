package com.faos.Booking.models;

import com.faos.Booking.models.enums.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bookings")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "customer", "cylinder", "bill"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    private int cylinderCount;

    @NotNull(message = "Time slot cannot be null.")
    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;

    @NotNull(message = "Delivery option cannot be null.")
    @Enumerated(EnumType.STRING)
    private DeliveryOption deliveryOption;

    @NotNull(message = "Payment mode cannot be null.")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private LocalDate deliveryDate;
    private LocalDate bookingDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;


    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bookings"})
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cylinder_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cylinder cylinder;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"booking"})
    private Bill bill;
}
