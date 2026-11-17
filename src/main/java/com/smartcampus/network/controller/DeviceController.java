package com.smartcampus.network.controller;

import com.smartcampus.network.model.Device;
import com.smartcampus.network.service.TopologyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "*")
public class DeviceController {

    private final TopologyService topologyService;

    public DeviceController(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(topologyService.getAllDevices());
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Device> toggleStatus(@PathVariable String id) {
        Optional<Device> toggled = topologyService.toggleDeviceStatus(id);
        return toggled.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
