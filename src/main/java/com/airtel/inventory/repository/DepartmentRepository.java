package com.airtel.inventory.repository;

import com.airtel.inventory.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

// @Repository → marks this as a Spring-managed database access component
// JpaRepository<Department, Long> →
//   - Department = the entity this repository manages
//   - Long = the data type of the primary key (id)
// By extending JpaRepository, we get these methods FOR FREE:
//   save(), findById(), findAll(), deleteById(), count(), existsById()
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Spring reads this method name and auto-generates:
    // SELECT * FROM departments WHERE name = ?
    Optional<Department> findByName(String name);

    // SELECT * FROM departments WHERE name LIKE %keyword%
    // Used for search functionality
    List<Department> findByNameContainingIgnoreCase(String keyword);
}