package com.airtel.inventory.service;

import com.airtel.inventory.model.Department;
import com.airtel.inventory.model.Employee;
import com.airtel.inventory.repository.DepartmentRepository;
import com.airtel.inventory.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    // Returns all employees — for the employee list page
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Returns only active employees — used when assigning devices
    // We don't want to assign devices to ex-employees
    public List<Employee> getActiveEmployees() {
        return employeeRepository.findByActive(true);
    }

    // Returns a single employee by id
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    // Saves a new employee after checking for duplicates
    public Employee saveEmployee(Employee employee) {

        // Business rule: employee ID must be unique across the company
        if (employeeRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            throw new RuntimeException(
                    "Employee ID already exists: " + employee.getEmployeeId());
        }

        // Business rule: email must be unique if provided
        if (employee.getEmail() != null &&
                employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new RuntimeException(
                    "Email already registered: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    // Updates an existing employee's details
    public Employee updateEmployee(Employee employee) {
        employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return employeeRepository.save(employee);
    }

    // Deactivates an employee instead of deleting them
    // Keeps all their assignment history intact for audit purposes
    public void deactivateEmployee(Long id) {
        Employee employee = getEmployeeById(id);
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    // Search employees by name — for the search bar
    public List<Employee> searchEmployees(String keyword) {
        return employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        keyword, keyword);
    }

    // Returns all employees in a specific department
    public List<Employee> getEmployeesByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        return employeeRepository.findByDepartment(department);
    }
}