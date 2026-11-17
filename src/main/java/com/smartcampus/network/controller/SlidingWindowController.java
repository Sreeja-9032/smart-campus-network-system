package com.smartcampus.network.controller;

import com.smartcampus.network.model.SlidingWindowRequest;
import com.smartcampus.network.model.SlidingWindowResponse;
import com.smartcampus.network.service.SlidingWindowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sliding-window")
@CrossOrigin(origins = "*")
public class SlidingWindowController {

    private final SlidingWindowService slidingWindowService;

    public SlidingWindowController(SlidingWindowService slidingWindowService) {
        this.slidingWindowService = slidingWindowService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<SlidingWindowResponse> simulate(@RequestBody SlidingWindowRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(SlidingWindowResponse.error("Request body is missing."));
        }
        SlidingWindowResponse response = slidingWindowService.simulate(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
