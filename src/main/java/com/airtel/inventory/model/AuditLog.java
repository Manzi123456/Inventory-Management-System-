package com.airtel.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// AuditLog records EVERY action done on ANY device
// This is the full history trail — nothing is ever deleted, only logged
@Data
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which device this log entry is about
    // nullable = false → every audit entry must relate to a device
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false)
    private String action;      // e.g. "DEVICE ISSUED", "DEVICE RETURNED", "STATUS CHANGED"

    private String performedBy; // which admin/user did this action
    private String details;     // full description of what changed

    // @Column(nullable = false) → every log must have a timestamp
    // LocalDateTime captures both date and time (e.g. 2025-04-21 14:30:00)
    @Column(nullable = false)
    private LocalDateTime timestamp; // exact moment this action happened

    // This method runs AUTOMATICALLY before saving to DB
    // It sets the timestamp so we never forget to set it manually
    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}