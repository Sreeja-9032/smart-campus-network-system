package com.smartcampus.network.service;

import com.smartcampus.network.model.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DijkstraRoutingService {

    private final TopologyService topologyService;

    public DijkstraRoutingService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    public DijkstraResponse calculateShortestPath(String sourceId, String destId) {
        if (sourceId == null || sourceId.trim().isEmpty() || destId == null || destId.trim().isEmpty()) {
            return DijkstraResponse.error("Source and destination must not be empty.");
        }

        sourceId = sourceId.trim();
        destId = destId.trim();

        Map<String, TopologyNode> nodeMap = topologyService.getNodeMap();

        // Resolve by ID or by Name (case-insensitive)
        String resolvedSource = resolveNodeId(sourceId, nodeMap);
        String resolvedDest = resolveNodeId(destId, nodeMap);

        if (resolvedSource == null) {
            return DijkstraResponse.error("Invalid source node: '" + sourceId + "'. Node not found in campus topology.");
        }
        if (resolvedDest == null) {
            return DijkstraResponse.error("Invalid destination node: '" + destId + "'. Node not found in campus topology.");
        }

        TopologyNode srcNode = nodeMap.get(resolvedSource);
        TopologyNode dstNode = nodeMap.get(resolvedDest);

        if (resolvedSource.equals(resolvedDest)) {
            DijkstraResponse resp = new DijkstraResponse();
            resp.setSuccess(true);
            resp.setMessage("Source and destination are the same device (" + srcNode.getName() + "). Routing cost is 0.");
            resp.setSource(resolvedSource);
            resp.setDestination(resolvedDest);
            resp.setSourceName(srcNode.getName());
            resp.setDestinationName(dstNode.getName());
            resp.setTotalCost(0);
            resp.setPathNodeIds(List.of(resolvedSource));
            resp.setPathNodeNames(List.of(srcNode.getName()));
            resp.setIntermediateDevices(Collections.emptyList());
            resp.setHops(Collections.emptyList());
            return resp;
        }

        // Build adjacency graph from topology edges (undirected)
        Map<String, Map<String, Integer>> graph = new HashMap<>();
        for (String nodeId : nodeMap.keySet()) {
            graph.put(nodeId, new HashMap<>());
        }

        for (TopologyEdge edge : topologyService.getEdges()) {
            if (graph.containsKey(edge.getFrom()) && graph.containsKey(edge.getTo())) {
                graph.get(edge.getFrom()).put(edge.getTo(), edge.getCost());
                graph.get(edge.getTo()).put(edge.getFrom(), edge.getCost());
            }
        }

        // Dijkstra's Algorithm
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        Set<String> visited = new HashSet<>();
        PriorityQueue<NodeDistance> pq = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));

        for (String node : nodeMap.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            previous.put(node, null);
        }

        distances.put(resolvedSource, 0);
        pq.add(new NodeDistance(resolvedSource, 0));

        while (!pq.isEmpty()) {
            NodeDistance current = pq.poll();
            String u = current.node;

            if (visited.contains(u)) continue;
            visited.add(u);

            if (u.equals(resolvedDest)) break;

            Map<String, Integer> neighbors = graph.getOrDefault(u, Collections.emptyMap());
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                String v = entry.getKey();
                int weight = entry.getValue();

                if (!visited.contains(v) && distances.get(u) != Integer.MAX_VALUE) {
                    int newDist = distances.get(u) + weight;
                    if (newDist < distances.get(v)) {
                        distances.put(v, newDist);
                        previous.put(v, u);
                        pq.add(new NodeDistance(v, newDist));
                    }
                }
            }
        }

        if (distances.get(resolvedDest) == Integer.MAX_VALUE) {
            return DijkstraResponse.error("No route found between " + srcNode.getName() + " and " + dstNode.getName() + ". Destination is unreachable.");
        }

        // Reconstruct path
        LinkedList<String> pathIds = new LinkedList<>();
        String curr = resolvedDest;
        while (curr != null) {
            pathIds.addFirst(curr);
            curr = previous.get(curr);
        }

        List<String> pathNames = new ArrayList<>();
        for (String id : pathIds) {
            TopologyNode node = nodeMap.get(id);
            pathNames.add(node != null ? node.getName() : id);
        }

        // Intermediate devices
        List<String> intermediate = new ArrayList<>();
        for (int i = 1; i < pathNames.size() - 1; i++) {
            intermediate.add(pathNames.get(i));
        }

        // Compute hops
        List<RouteHop> hops = new ArrayList<>();
        int cumCost = 0;
        for (int i = 0; i < pathIds.size() - 1; i++) {
            String fromId = pathIds.get(i);
            String toId = pathIds.get(i + 1);
            int cost = graph.get(fromId).get(toId);
            cumCost += cost;
            hops.add(new RouteHop(
                    fromId,
                    nodeMap.get(fromId).getName(),
                    toId,
                    nodeMap.get(toId).getName(),
                    cost,
                    cumCost
            ));
        }

        DijkstraResponse response = new DijkstraResponse();
        response.setSuccess(true);
        response.setMessage("Optimal shortest path found with total metric cost: " + distances.get(resolvedDest));
        response.setSource(resolvedSource);
        response.setDestination(resolvedDest);
        response.setSourceName(srcNode.getName());
        response.setDestinationName(dstNode.getName());
        response.setTotalCost(distances.get(resolvedDest));
        response.setPathNodeIds(pathIds);
        response.setPathNodeNames(pathNames);
        response.setIntermediateDevices(intermediate);
        response.setHops(hops);

        return response;
    }

    private String resolveNodeId(String input, Map<String, TopologyNode> nodeMap) {
        if (nodeMap.containsKey(input)) return input;
        for (Map.Entry<String, TopologyNode> entry : nodeMap.entrySet()) {
            if (entry.getValue().getName().equalsIgnoreCase(input) ||
                entry.getKey().equalsIgnoreCase(input)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static class NodeDistance {
        String node;
        int distance;

        NodeDistance(String node, int distance) {
            this.node = node;
            this.distance = distance;
        }
    }
}
