package com.airtel.inventory.service;

import com.airtel.inventory.model.AuditLog;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Device.DeviceCondition;
import com.airtel.inventory.model.Device.DeviceStatus;
import com.airtel.inventory.repository.AuditLogRepository;
import com.airtel.inventory.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// @Transactional → ensures that if anything fails mid-operation,
// ALL database changes are rolled back — data stays consistent
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;

    private final AuditLogRepository auditLogRepository;

    // Returns all devices — used for the main device list page
    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    // Returns a single device — used for detail and edit pages
    public Device getDeviceById(Long id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found with id: " + id));
    }

    // Registers a new device AND logs the action automatically
    // performedBy = the admin's name who is registering the device
    public Device registerDevice(Device device, String performedBy) {

        // Business rule: asset tag must be unique — check before saving
        if (deviceRepository.findByAssetTag(device.getAssetTag()).isPresent()) {
            throw new RuntimeException("Asset tag already exists: " + device.getAssetTag());
        }

        // New devices start as AVAILABLE by default
        device.setStatus(DeviceStatus.AVAILABLE);

        // Save the device to the database
        Device savedDevice = deviceRepository.save(device);

        // Immediately create an audit log entry for this registration
        logAction(savedDevice, "DEVICE REGISTERED",
                "New device registered: " + device.getBrand() + " " + device.getModel(),
                performedBy);

        return savedDevice;
    }

    // Updates an existing device's details and logs the change
    public Device updateDevice(Device device, String performedBy) {
        deviceRepository.findById(device.getId())
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Device updatedDevice = deviceRepository.save(device);

        logAction(updatedDevice, "DEVICE UPDATED",
                "Device details updated for: " + device.getAssetTag(),
                performedBy);

        return updatedDevice;
    }

    // Updates ONLY the condition of a device e.g. from GOOD to DAMAGED
    // Separate method because condition changes happen frequently
    public void updateDeviceCondition(Long deviceId, DeviceCondition newCondition,
                                      String performedBy) {
        Device device = getDeviceById(deviceId);
        DeviceCondition oldCondition = device.getCondition();
        device.setCondition(newCondition);
        deviceRepository.save(device);

        logAction(device, "CONDITION CHANGED",
                "Condition changed from " + oldCondition + " to " + newCondition,
                performedBy);
    }

    // Retires a device — marks it as no longer in use
    // Does NOT delete it — keeps it in DB for reporting history
    public void retireDevice(Long deviceId, String performedBy) {
        Device device = getDeviceById(deviceId);

        // Business rule: cannot retire a device that is currently assigned
        if (device.getStatus() == DeviceStatus.ASSIGNED) {
            throw new RuntimeException(
                    "Cannot retire device that is currently assigned. Return it first.");
        }

        device.setStatus(DeviceStatus.RETIRED);
        deviceRepository.save(device);

        logAction(device, "DEVICE RETIRED",
                "Device marked as retired: " + device.getAssetTag(),
                performedBy);
    }

    // Search across all device fields — for the search bar
    public List<Device> searchDevices(String keyword) {
        return deviceRepository.searchDevices(keyword);
    }

    // Filter by status — e.g. show only AVAILABLE devices
    public List<Device> getDevicesByStatus(DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }

    // Filter by type — e.g. show only Laptops
    public List<Device> getDevicesByType(String type) {
        return deviceRepository.findByDeviceType(type);
    }

    // Dashboard summary counts — how many devices per status
    public long countByStatus(DeviceStatus status) {
        return deviceRepository.countByStatus(status);
    }

    // Checks if an asset tag is already taken — used in form validation
    public boolean assetTagExists(String assetTag) {
        return deviceRepository.findByAssetTag(assetTag).isPresent();
    }

    // Private helper method — creates and saves an AuditLog entry
    // Called internally after every device action above
    // Private because only THIS service should call it
    private void logAction(Device device, String action,
                           String details, String performedBy) {
        AuditLog log = new AuditLog();
        log.setDevice(device);
        log.setAction(action);
        log.setDetails(details);
        log.setPerformedBy(performedBy);
        // timestamp is set automatically by @PrePersist in AuditLog entity
        auditLogRepository.save(log);
    }
}