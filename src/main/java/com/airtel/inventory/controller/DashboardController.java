package com.airtel.inventory.controller;

import com.airtel.inventory.model.Device.DeviceStatus;
import com.airtel.inventory.service.AssignmentService;
import com.airtel.inventory.service.AuditLogService;
import com.airtel.inventory.service.DeviceService;
import com.airtel.inventory.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class DashboardController {

        private final DeviceService deviceService;
        private final EmployeeService employeeService;
        private final AssignmentService assignmentService;
        private final AuditLogService auditLogService;

        @GetMapping({"/", "/."})
    public String dashboard(HttpSession session, Model model) {
        // Check if user is logged in
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        model.addAttribute("totalDevices",
                deviceService.getAllDevices().size());
        model.addAttribute("availableDevices",
                deviceService.countByStatus(DeviceStatus.AVAILABLE));
        model.addAttribute("assignedDevices",
                deviceService.countByStatus(DeviceStatus.ASSIGNED));
        model.addAttribute("underRepairDevices",
                deviceService.countByStatus(DeviceStatus.UNDER_REPAIR));
        model.addAttribute("retiredDevices",
                deviceService.countByStatus(DeviceStatus.RETIRED));
        model.addAttribute("totalEmployees",
                employeeService.getAllEmployees().size());
        model.addAttribute("activeAssignments",
                assignmentService.getActiveAssignments().size());
        model.addAttribute("recentActivity",
                auditLogService.getRecentActivity());
        
        // Add user info to model
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("userRole", session.getAttribute("userRole"));
        
        return "dashboard";
    }
}