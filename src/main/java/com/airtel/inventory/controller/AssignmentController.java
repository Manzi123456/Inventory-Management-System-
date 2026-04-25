package com.airtel.inventory.controller;

import com.airtel.inventory.model.Device.DeviceStatus;
import com.airtel.inventory.service.AssignmentService;
import com.airtel.inventory.service.DeviceService;
import com.airtel.inventory.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final DeviceService deviceService;
    private final EmployeeService employeeService;

    @GetMapping
    public String listAssignments(Model model) {
        model.addAttribute("assignments", assignmentService.getActiveAssignments());
        return "assignments/list";
    }

    @GetMapping("/history")
    public String assignmentHistory(Model model) {
        model.addAttribute("assignments", assignmentService.getAllAssignments());
        return "assignments/history";
    }

    @GetMapping("/new")
    public String showIssueForm(Model model) {
        model.addAttribute("availableDevices",
                deviceService.getDevicesByStatus(DeviceStatus.AVAILABLE));
        model.addAttribute("activeEmployees",
                employeeService.getActiveEmployees());
        return "assignments/form";
    }

    @PostMapping("/issue")
    public String issueDevice(@RequestParam Long deviceId,
                              @RequestParam Long employeeId,
                              @RequestParam String issuedBy,
                              @RequestParam(required = false) String purpose,
                              @RequestParam(required = false) String notes,
                              RedirectAttributes redirectAttributes) {
        try {
            assignmentService.issueDevice(deviceId, employeeId,
                    issuedBy, purpose, notes);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Device issued successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/assignments";
    }

    @GetMapping("/return/{id}")
    public String showReturnForm(@PathVariable Long id, Model model) {
        model.addAttribute("assignment", assignmentService.getAssignmentById(id));
        model.addAttribute("statuses", DeviceStatus.values());
        return "assignments/return";
    }

    @PostMapping("/return")
    public String returnDevice(@RequestParam Long assignmentId,
                               @RequestParam String returnedTo,
                               @RequestParam(required = false) String notes,
                               @RequestParam DeviceStatus newStatus,
                               RedirectAttributes redirectAttributes) {
        try {
            assignmentService.returnDevice(assignmentId, returnedTo,
                    notes, newStatus);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Device returned successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/assignments";
    }
}