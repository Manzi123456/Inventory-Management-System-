package com.airtel.inventory.repository;

import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Gets full history of a specific device — all actions ever done on it
    // Returns ordered by timestamp descending (newest first)
    List<AuditLog> findByDeviceOrderByTimestampDesc(Device device);

    // Gets all actions performed by a specific admin/user
    // Used to track what each admin has done — accountability
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);

    // Gets all logs for a specific action type e.g. all "DEVICE ISSUED" events
    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    // Gets all audit logs within a date range — for audit reports
    // Used in the reports module to filter history by date
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime start, LocalDateTime end);

    // Gets the most recent 10 actions across all devices
    // Used on the dashboard to show "Recent Activity" section
    List<AuditLog> findTop10ByOrderByTimestampDesc();
}