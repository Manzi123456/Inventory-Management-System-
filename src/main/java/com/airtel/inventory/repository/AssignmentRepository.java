package com.airtel.inventory.repository;

import com.airtel.inventory.model.Assignment;
import com.airtel.inventory.model.Assignment.AssignmentStatus;
import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    // Finds all assignments for a specific device
    // Used to see full history of who had this device
    List<Assignment> findByDevice(Device device);

    // Finds all assignments for a specific employee
    // Used to see all devices this employee has ever had
    List<Assignment> findByEmployee(Employee employee);

    // Finds all currently ACTIVE assignments — devices not yet returned
    List<Assignment> findByStatus(AssignmentStatus status);

    // Finds the current active assignment for a specific device
    // Optional because the device might not be currently assigned
    // Used when processing a return — find who currently has it
    Optional<Assignment> findByDeviceAndStatus(Device device, AssignmentStatus status);

    // Finds all active assignments for a specific employee
    // An employee could have multiple devices (laptop + phone)
    List<Assignment> findByEmployeeAndStatus(Employee employee, AssignmentStatus status);

    // Finds assignments issued between two dates — for date range reports
    List<Assignment> findByIssueDateBetween(LocalDate startDate, LocalDate endDate);

    // @Query → counts how many devices a specific employee currently has
    // This is used to enforce device limits per employee if needed
    @Query("SELECT COUNT(a) FROM Assignment a WHERE " +
            "a.employee = :employee AND a.status = 'ACTIVE'")
    long countActiveAssignmentsByEmployee(@Param("employee") Employee employee);

    // Finds all assignments for a department through the employee relationship
    // Used for department-level reports
    @Query("SELECT a FROM Assignment a WHERE " +
            "a.employee.department.id = :departmentId AND a.status = :status")
    List<Assignment> findByEmployeeDepartmentIdAndStatus(
            @Param("departmentId") Long departmentId,
            @Param("status") AssignmentStatus status);
}