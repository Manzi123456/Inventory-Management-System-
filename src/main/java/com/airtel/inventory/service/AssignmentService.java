package com.airtel.inventory.service;

import com.airtel.inventory.model.Assignment;
import com.airtel.inventory.model.Assignment.AssignmentStatus;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Device.DeviceStatus;
import com.airtel.inventory.model.Employee;
import com.airtel.inventory.repository.AssignmentRepository;
import com.airtel.inventory.repository.DeviceRepository;
import com.airtel.inventory.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final DeviceRepository deviceRepository;
    private final EmployeeRepository employeeRepository;
    private final DeviceService deviceService;

    // Issues a device to an employee — the core business operation
    public Assignment issueDevice(Long deviceId, Long employeeId,
                                  String issuedBy, String purpose, String notes) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Business rule: only AVAILABLE devices can be issued
        if (device.getStatus() != DeviceStatus.AVAILABLE) {
            throw new RuntimeException(
                    "Device is not available for assignment. Current status: "
                            + device.getStatus());
        }

        // Business rule: only active employees can receive devices
        if (!employee.isActive()) {
            throw new RuntimeException(
                    "Cannot assign device to an inactive employee.");
        }

        // Create the assignment record
        Assignment assignment = new Assignment();
        assignment.setDevice(device);
        assignment.setEmployee(employee);
        assignment.setIssueDate(LocalDate.now()); // today's date automatically
        assignment.setIssuedBy(issuedBy);
        assignment.setPurpose(purpose);
        assignment.setNotes(notes);
        assignment.setStatus(AssignmentStatus.ACTIVE);

        // Update the device status to ASSIGNED
        device.setStatus(DeviceStatus.ASSIGNED);
        deviceRepository.save(device);

        // Save the assignment
        Assignment saved = assignmentRepository.save(assignment);

        // Log this action in audit trail
        deviceService.updateDevice(device, issuedBy);

        return saved;
    }

    // Processes a device return — reverses the assignment
    public Assignment returnDevice(Long assignmentId, String returnedTo,
                                   String notes, DeviceStatus newStatus) {

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        // Business rule: can only return an ACTIVE assignment
        if (assignment.getStatus() != AssignmentStatus.ACTIVE) {
            throw new RuntimeException("This assignment is already closed.");
        }

        // Mark the assignment as returned and set return details
        assignment.setStatus(AssignmentStatus.RETURNED);
        assignment.setReturnDate(LocalDate.now()); // today's date automatically
        assignment.setReturnedTo(returnedTo);
        assignment.setNotes(notes);

        // Update the device status based on its condition after return
        // Could be AVAILABLE (good), UNDER_REPAIR (damaged), etc.
        Device device = assignment.getDevice();
        device.setStatus(newStatus);
        deviceRepository.save(device);

        return assignmentRepository.save(assignment);
    }

    // Returns all currently active assignments — devices out in the field
    public List<Assignment> getActiveAssignments() {
        return assignmentRepository.findByStatus(AssignmentStatus.ACTIVE);
    }

    // Returns full assignment history — all records ever
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // Returns assignment history for one specific device
    public List<Assignment> getAssignmentsByDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));
        return assignmentRepository.findByDevice(device);
    }

    // Returns all assignments for one specific employee
    public List<Assignment> getAssignmentsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return assignmentRepository.findByEmployee(employee);
    }

    // Returns assignments filtered by date range — for reports
    public List<Assignment> getAssignmentsByDateRange(LocalDate start, LocalDate end) {
        return assignmentRepository.findByIssueDateBetween(start, end);
    }

    // Returns a single assignment — for detail/return pages
    public Assignment getAssignmentById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }
}