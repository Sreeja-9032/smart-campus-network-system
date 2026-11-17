package com.smartcampus.network.controller;

import com.smartcampus.network.model.Ns2MetricsResponse;
import com.smartcampus.network.model.Ns2TraceImportRequest;
import com.smartcampus.network.service.Ns2MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Ns2MetricsController {

    private final Ns2MetricsService ns2MetricsService;

    public Ns2MetricsController(Ns2MetricsService ns2MetricsService) {
        this.ns2MetricsService = ns2MetricsService;
    }

    @GetMapping("/metrics/sample-trace")
    public ResponseEntity<String> getSampleTrace() {
        return ResponseEntity.ok(ns2MetricsService.generateSampleNs2Trace());
    }

    @PostMapping("/metrics/analyze")
    public ResponseEntity<Ns2MetricsResponse> analyzeTrace(@RequestBody(required = false) Ns2TraceImportRequest request) {
        if (request == null || request.getTraceContent() == null || request.getTraceContent().trim().isEmpty()) {
            // Run sample simulation when no trace provided
            return ResponseEntity.ok(ns2MetricsService.runSampleSimulation());
        }
        Ns2MetricsResponse response = ns2MetricsService.analyzeImportedTrace(request.getTraceContent());
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ns2/tcl-script")
    public ResponseEntity<String> getTclScript() {
        return ResponseEntity.ok(ns2MetricsService.getTclScript());
    }
}
