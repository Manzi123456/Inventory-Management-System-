package com.airtel.inventory.service;

import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.repository.AuditLogRepository;
import com.airtel.inventory.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final DeviceRepository deviceRepository;

    // Returns complete audit history for one device — newest first
    // Used on the device detail page to show full movement history
    public List<AuditLog> getDeviceHistory(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        return auditLogRepository.findByDeviceOrderByTimestampDesc(device);
    }

    // Returns all actions performed by a specific admin
    // Used for admin accountability reporting
    public List<AuditLog> getLogsByAdmin(String performedBy) {
        return auditLogRepository.findByPerformedByOrderByTimestampDesc(performedBy);
    }

    // Returns all logs within a date-time range — for audit reports
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetweenOrderByTimestampDesc(start, end);
    }

    // Returns the 10 most recent actions across ALL devices
    // Used on the dashboard "Recent Activity" section
    public List<AuditLog> getRecentActivity() {
        return auditLogRepository.findTop10ByOrderByTimestampDesc();
    }

    // Returns all logs — for the full audit log report page
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }
}