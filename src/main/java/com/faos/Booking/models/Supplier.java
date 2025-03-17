package com.faos.Booking.models;

import com.faos.Booking.models.enums.SupplierStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "suppliers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "cylinders"})
@ToString(exclude = "cylinders") // Avoid recursive toString() call
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    @NotBlank(message = "Supplier name cannot be blank.")
    private String supplierName;

    @NotBlank(message = "Phone number cannot be blank.")
    private String phoneNo;

    @Email(message = "Email should be valid.")
    private String email;

    private String address;

    @NotBlank(message = "License number cannot be blank.")
    private String licenseNumber;

    @NotNull(message = "Supplier status cannot be null.")
    @Enumerated(EnumType.STRING)
    private SupplierStatus supplierStatus = SupplierStatus.ACTIVE;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("supplier")
    private List<Cylinder> cylinders;

    // Total cylinder count (all cylinders)
    public int getCylinderCount() {
        return (cylinders != null) ? cylinders.size() : 0;
    }

    // New: Return only the count of AVAILABLE cylinders
    public int getAvailableCylinderCount() {
        if (cylinders == null) return 0;
        return (int) cylinders.stream()
                .filter(cylinder -> cylinder.getStatus() == com.faos.Booking.models.enums.CylinderStatus.AVAILABLE)
                .count();
    }

}
