package com.smartcampus.network.controller;

import com.smartcampus.network.model.PacketAnalysisResponse;
import com.smartcampus.network.model.PacketImportRequest;
import com.smartcampus.network.service.PacketAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packets")
@CrossOrigin(origins = "*")
public class PacketController {

    private final PacketAnalysisService packetAnalysisService;

    public PacketController(PacketAnalysisService packetAnalysisService) {
        this.packetAnalysisService = packetAnalysisService;
    }

    @GetMapping("/generate")
    public ResponseEntity<PacketAnalysisResponse> generateSamplePackets() {
        return ResponseEntity.ok(packetAnalysisService.generateSampleCampusPackets());
    }

    @PostMapping("/import")
    public ResponseEntity<PacketAnalysisResponse> importWiresharkCsv(@RequestBody PacketImportRequest request) {
        if (request == null || request.getCsvContent() == null || request.getCsvContent().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(PacketAnalysisResponse.error("CSV content is empty."));
        }
        PacketAnalysisResponse response = packetAnalysisService.parseWiresharkCsv(request.getCsvContent());
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
