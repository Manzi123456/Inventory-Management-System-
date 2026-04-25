package com.airtel.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false, unique = true) →
    // Asset tag is like a serial number — must exist and must be unique per device
    @Column(nullable = false, unique = true)
    private String assetTag;        // e.g. "AIR-LT-001"

    // nullable = false → every device must have a type
    @Column(nullable = false)
    private String deviceType;      // "Laptop", "Desktop", "Mobile Phone"

    @Column(nullable = false)
    private String brand;           // e.g. "Dell", "HP", "Samsung"

    private String model;           // e.g. "Latitude 5520"
    private String serialNumber;    // manufacturer serial number
    private String processor;       // e.g. "Intel Core i5"
    private String ram;             // e.g. "8GB"
    private String storage;         // e.g. "256GB SSD"
    private String operatingSystem; // e.g. "Windows 11"

    // @Enumerated(EnumType.STRING) →
    // Stores the enum value as text in DB ("AVAILABLE") not as a number (0)
    // Much more readable in the database
    @Enumerated(EnumType.STRING)
    @Column(name = "device_status")
    private DeviceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_condition")
    private DeviceCondition condition;

    private LocalDate purchaseDate;     // when Airtel bought this device
    private LocalDate warrantyExpiry;   // when warranty ends
    private String notes;               // any extra information

    // -- Enum definitions inside the same file for simplicity --

    // These enums define the allowed values for status and condition fields
    public enum DeviceStatus {
        AVAILABLE,      // in stock, not assigned to anyone
        ASSIGNED,       // currently with an employee
        UNDER_REPAIR,   // sent for maintenance
        RETIRED         // no longer in use
    }

    public enum DeviceCondition {
        NEW,        // brand new, just purchased
        GOOD,       // working well, minor wear
        FAIR,       // working but noticeable wear
        DAMAGED     // has issues or physical damage
    }
}