package com.smartcampus.network.controller;

import com.smartcampus.network.model.DijkstraRequest;
import com.smartcampus.network.model.DijkstraResponse;
import com.smartcampus.network.model.NetworkTopology;
import com.smartcampus.network.service.DijkstraRoutingService;
import com.smartcampus.network.service.TopologyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class NetworkTopologyController {

    private final TopologyService topologyService;
    private final DijkstraRoutingService dijkstraRoutingService;

    public NetworkTopologyController(TopologyService topologyService, DijkstraRoutingService dijkstraRoutingService) {
        this.topologyService = topologyService;
        this.dijkstraRoutingService = dijkstraRoutingService;
    }

    @GetMapping("/network/topology")
    public ResponseEntity<NetworkTopology> getTopology() {
        return ResponseEntity.ok(topologyService.getTopology());
    }

    @PostMapping("/routing/dijkstra")
    public ResponseEntity<DijkstraResponse> calculateDijkstra(@RequestBody DijkstraRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(DijkstraResponse.error("Request body is missing."));
        }
        DijkstraResponse response = dijkstraRoutingService.calculateShortestPath(request.getSource(), request.getDestination());
        if (!response.isSuccess()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
