package com.smartcampus.network.controller;

import com.smartcampus.network.model.CrcRequest;
import com.smartcampus.network.model.CrcResponse;
import com.smartcampus.network.model.CrcVerifyRequest;
import com.smartcampus.network.model.CrcVerifyResponse;
import com.smartcampus.network.service.CrcService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crc")
@CrossOrigin(origins = "*")
public class CrcController {

    private final CrcService crcService;

    public CrcController(CrcService crcService) {
        this.crcService = crcService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<CrcResponse> calculateCrc(@RequestBody CrcRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(CrcResponse.error("Request body is missing."));
        }
        CrcResponse response = crcService.calculateCrc(request);
        if (!response.isValid()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<CrcVerifyResponse> verifyCrc(@RequestBody CrcVerifyRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(CrcVerifyResponse.formatError("Request body is missing."));
        }
        CrcVerifyResponse response = crcService.verifyCrc(request);
        if (!response.isValidFormat()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
