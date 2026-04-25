package com.airtel.inventory.repository;

import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Device.DeviceCondition;
import com.airtel.inventory.model.Device.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    // Finds a device by its unique asset tag e.g. "AIR-LT-001"
    // Used when checking if an asset tag already exists
    Optional<Device> findByAssetTag(String assetTag);

    // Finds all devices of a specific type e.g. all "Laptop" devices
    List<Device> findByDeviceType(String deviceType);

    // Finds all devices with a specific status e.g. all AVAILABLE devices
    List<Device> findByStatus(DeviceStatus status);

    // Finds all devices with a specific condition e.g. all DAMAGED devices
    List<Device> findByCondition(DeviceCondition condition);

    // Finds devices by brand e.g. all "Dell" devices
    List<Device> findByBrandIgnoreCase(String brand);

    // @Query → used when the method name alone can't express the query
    // JPQL (Java Persistence Query Language) — uses class names not table names
    // This searches across multiple fields at once for the search bar feature
    @Query("SELECT d FROM Device d WHERE " +
            "LOWER(d.assetTag) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.deviceType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    // @Param("keyword") → binds the method parameter to :keyword in the query
    List<Device> searchDevices(@Param("keyword") String keyword);

    // Counts devices grouped by status — used for dashboard summary cards
    // Returns how many AVAILABLE, ASSIGNED, etc. devices exist
    long countByStatus(DeviceStatus status);

    // Counts devices grouped by type — used for reports
    long countByDeviceType(String deviceType);
}