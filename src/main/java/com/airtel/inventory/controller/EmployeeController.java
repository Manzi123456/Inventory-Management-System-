package com.airtel.inventory.controller;

import com.airtel.inventory.model.Employee;
import com.airtel.inventory.service.DepartmentService;
import com.airtel.inventory.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String search,
                                @RequestParam(required = false) String active,
                                Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("employees", employeeService.searchEmployees(search));
            model.addAttribute("search", search);
        } else if ("true".equals(active)) {
            model.addAttribute("employees", employeeService.getActiveEmployees());
        } else {
            model.addAttribute("employees", employeeService.getAllEmployees());
        }
        return "employees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employees/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "employees/form";
    }

    @PostMapping("/save")
    public String saveEmployee(@Valid @ModelAttribute Employee employee,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "employees/form";
        }
        try {
            if (employee.getId() == null) {
                employeeService.saveEmployee(employee);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Employee registered successfully!");
            } else {
                employeeService.updateEmployee(employee);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Employee updated successfully!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employees";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateEmployee(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        try {
            employeeService.deactivateEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Employee deactivated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/employees";
    }
}