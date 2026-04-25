package com.airtel.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // unique = true → no two employees share the same employee ID
    @Column(nullable = false, unique = true)
    private String employeeId;   // e.g. "EMP-045" — Airtel staff ID

    private String email;
    private String phone;
    private String jobTitle;     // e.g. "Network Engineer"

    // @ManyToOne → many employees can belong to ONE department
    // @JoinColumn → creates a foreign key column "department_id" in employees table
    // This links Employee to Department in the database
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private boolean active = true; // false = employee has left the company
}