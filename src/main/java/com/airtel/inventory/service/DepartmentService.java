package com.airtel.inventory.service;

import com.airtel.inventory.model.Department;
import com.airtel.inventory.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

// @Service → marks this as a Spring-managed service component
// Spring will automatically create one instance and inject it wherever needed

// @RequiredArgsConstructor (Lombok) → automatically generates a constructor
// for all 'final' fields — this is how Spring injects the repository
// No need to write @Autowired or a manual constructor
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    // Returns all departments — used to populate dropdowns in forms
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Returns a single department by id — used in edit forms
    // orElseThrow → if not found, throws an exception with a clear message
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    // Saves a new department or updates an existing one
    // JPA's save() handles both: if id exists = update, if no id = insert
    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    // Deletes a department by id
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    // Search departments by keyword — for search bar
    public List<Department> searchDepartments(String keyword) {
        return departmentRepository.findByNameContainingIgnoreCase(keyword);
    }

    // Checks if a department name already exists — prevents duplicates
    // Used in the form validation before saving
    public boolean departmentExists(String name) {
        return departmentRepository.findByName(name).isPresent();
    }
}