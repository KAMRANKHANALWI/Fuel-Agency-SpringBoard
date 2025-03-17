package com.faos.Booking.models;

import com.faos.Booking.models.enums.CylinderStatus;
import com.faos.Booking.models.enums.CylinderType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cylinders")
@ToString(exclude = "supplier") // Exclude supplier to prevent recursive toString
public class Cylinder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cylinderId;

    @NotNull(message = "Cylinder type is required.")
    @Enumerated(EnumType.STRING)
    private CylinderType cylinderType = CylinderType.DOMESTIC; // Default to DOMESTIC

    @NotNull(message = "Cylinder status is required.")
    @Enumerated(EnumType.STRING)
    private CylinderStatus status = CylinderStatus.AVAILABLE;

    @Temporal(TemporalType.DATE)
    private LocalDate lastRefillDate;

    @Temporal(TemporalType.DATE)
    private LocalDate nextRefillDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonIgnoreProperties("cylinders")
    private Supplier supplier;

    @OneToMany(mappedBy = "cylinder")
    @JsonIgnoreProperties("cylinder")
    private List<Booking> bookings;
}
