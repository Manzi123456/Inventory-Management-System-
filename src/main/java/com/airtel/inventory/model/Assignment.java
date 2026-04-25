package com.airtel.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

// This entity tracks WHO has WHICH device and WHEN
// Every time a device is issued or returned, a record is created here
@Data
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @ManyToOne → many assignments can involve the same device (over time)
    // @JoinColumn(nullable = false) → every assignment MUST have a device
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    // @ManyToOne → many assignments can belong to same employee
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(nullable = false)
    private LocalDate issueDate;    // date device was handed to employee

    private LocalDate returnDate;   // null means device is still with employee

    private String issuedBy;        // name of the admin who issued the device
    private String returnedTo;      // name of admin who received it back
    private String purpose;         // reason for assignment e.g. "Field work"
    private String notes;           // any remarks at time of issue or return

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status")
    private AssignmentStatus status;

    public enum AssignmentStatus {
        ACTIVE,     // device is currently with the employee
        RETURNED    // device has been handed back
    }
}