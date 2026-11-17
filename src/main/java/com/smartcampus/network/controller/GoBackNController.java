package com.smartcampus.network.controller;

import com.smartcampus.network.model.GoBackNRequest;
import com.smartcampus.network.model.GoBackNResponse;
import com.smartcampus.network.service.GoBackNService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gobackn")
@CrossOrigin(origins = "*")
public class GoBackNController {

    private final GoBackNService goBackNService;

    public GoBackNController(GoBackNService goBackNService) {
        this.goBackNService = goBackNService;
    }

    @PostMapping("/simulate")
    public ResponseEntity<GoBackNResponse> simulate(@RequestBody GoBackNRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(GoBackNResponse.error("Request body is missing."));
        }
        GoBackNResponse response = goBackNService.simulate(request);
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
