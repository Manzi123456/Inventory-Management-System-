package com.airtel.inventory.repository;

import com.airtel.inventory.model.Department;
import com.airtel.inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Finds employee by their Airtel staff ID e.g. "EMP-045"
    Optional<Employee> findByEmployeeId(String employeeId);

    // Finds employee by email — useful for checking duplicates
    Optional<Employee> findByEmail(String email);

    // Finds all employees in a specific department
    // Spring understands "ByDepartment" maps to the department field in Employee
    List<Employee> findByDepartment(Department department);

    // Finds only active or inactive employees
    // active=true returns current staff, active=false returns ex-employees
    List<Employee> findByActive(boolean active);

    // Searches employees by first name or last name — for the search bar
    List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    // Finds all active employees in a specific department
    // Used when assigning a device — show only active staff in selected dept
    List<Employee> findByDepartmentAndActive(Department department, boolean active);
}