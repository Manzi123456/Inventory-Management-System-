package com.airtel.inventory.controller;

import com.airtel.inventory.model.Device;
import com.airtel.inventory.model.Device.DeviceCondition;
import com.airtel.inventory.model.Device.DeviceStatus;
import com.airtel.inventory.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    public String listDevices(@RequestParam(required = false) String search,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String type,
                              Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("devices", deviceService.searchDevices(search));
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            model.addAttribute("devices",
                    deviceService.getDevicesByStatus(DeviceStatus.valueOf(status)));
            model.addAttribute("selectedStatus", status);
        } else if (type != null && !type.isEmpty()) {
            model.addAttribute("devices", deviceService.getDevicesByType(type));
            model.addAttribute("selectedType", type);
        } else {
            model.addAttribute("devices", deviceService.getAllDevices());
        }
        model.addAttribute("statuses", DeviceStatus.values());
        model.addAttribute("deviceTypes",
                new String[]{"Laptop", "Desktop", "Mobile Phone"});
        return "devices/list";
    }

    @GetMapping("/{id}")
    public String viewDevice(@PathVariable Long id, Model model) {
        model.addAttribute("device", deviceService.getDeviceById(id));
        return "devices/detail";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("device", new Device());
        model.addAttribute("statuses", DeviceStatus.values());
        model.addAttribute("conditions", DeviceCondition.values());
        model.addAttribute("deviceTypes",
                new String[]{"Laptop", "Desktop", "Mobile Phone"});
        return "devices/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("device", deviceService.getDeviceById(id));
        model.addAttribute("statuses", DeviceStatus.values());
        model.addAttribute("conditions", DeviceCondition.values());
        model.addAttribute("deviceTypes",
                new String[]{"Laptop", "Desktop", "Mobile Phone"});
        return "devices/form";
    }

    @PostMapping("/save")
    public String saveDevice(@Valid @ModelAttribute Device device,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", DeviceStatus.values());
            model.addAttribute("conditions", DeviceCondition.values());
            model.addAttribute("deviceTypes",
                    new String[]{"Laptop", "Desktop", "Mobile Phone"});
            return "devices/form";
        }
        try {
            if (device.getId() == null) {
                deviceService.registerDevice(device, "admin");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Device registered successfully!");
            } else {
                deviceService.updateDevice(device, "admin");
                redirectAttributes.addFlashAttribute("successMessage",
                        "Device updated successfully!");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/devices";
    }

    @PostMapping("/condition")
    public String updateCondition(@RequestParam Long deviceId,
                                  @RequestParam DeviceCondition condition,
                                  RedirectAttributes redirectAttributes) {
        try {
            deviceService.updateDeviceCondition(deviceId, condition, "admin");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Device condition updated!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/devices/" + deviceId;
    }

    @GetMapping("/retire/{id}")
    public String retireDevice(@PathVariable Long id,
                               RedirectAttributes redirectAttributes) {
        try {
            deviceService.retireDevice(id, "admin");
            redirectAttributes.addFlashAttribute("successMessage",
                    "Device retired successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/devices";
    }
}