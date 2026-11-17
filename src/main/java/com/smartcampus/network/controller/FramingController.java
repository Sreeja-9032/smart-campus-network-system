package com.smartcampus.network.controller;

import com.smartcampus.network.model.DeframingRequest;
import com.smartcampus.network.model.DeframingResponse;
import com.smartcampus.network.model.FramingRequest;
import com.smartcampus.network.model.FramingResponse;
import com.smartcampus.network.service.FramingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/framing")
@CrossOrigin(origins = "*")
public class FramingController {

    private final FramingService framingService;

    public FramingController(FramingService framingService) {
        this.framingService = framingService;
    }

    @PostMapping("/encode")
    public ResponseEntity<FramingResponse> encodeMessage(@RequestBody FramingRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(FramingResponse.error("Request body is missing."));
        }
        FramingResponse response = framingService.encodeMessage(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decode")
    public ResponseEntity<DeframingResponse> decodeFrames(@RequestBody DeframingRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(DeframingResponse.error("Request body is missing."));
        }
        DeframingResponse response = framingService.decodeFrames(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
