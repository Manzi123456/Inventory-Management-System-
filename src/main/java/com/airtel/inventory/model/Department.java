package com.airtel.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

// @Data (Lombok) → auto-generates getters, setters, toString, equals, hashCode
// No need to write them manually
@Data

// @Entity → tells JPA this class maps to a database table
@Entity

// @Table → specifies the exact table name in the database
@Table(name = "departments")
public class Department {

    // @Id → this field is the primary key
    // @GeneratedValue → auto-increment (1, 2, 3...) handled by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(nullable = false, unique = true) →
    // - nullable = false: this column cannot be empty in the DB
    // - unique = true: no two departments can have the same name
    @Column(nullable = false, unique = true)
    private String name;        // e.g. "IT", "Finance", "HR"

    private String description; // optional notes about the department
}