package com.smartcampus.network.service;

import com.smartcampus.network.model.Device;
import com.smartcampus.network.model.NetworkTopology;
import com.smartcampus.network.model.TopologyEdge;
import com.smartcampus.network.model.TopologyNode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TopologyService {

    private final Map<String, TopologyNode> nodeMap = new ConcurrentHashMap<>();
    private final List<TopologyEdge> edges = new ArrayList<>();
    private final Map<String, Device> deviceMap = new ConcurrentHashMap<>();

    public TopologyService() {
        initializeCampusTopology();
    }

    private void initializeCampusTopology() {
        // 1. Departments & Endpoints
        addNode("CS_DEPT", "Computer Science", "192.168.1.10", "Department Host", "ONLINE", 8, "💻", "CS Dept Workstations & Lab", List.of("SW_CS"));
        addNode("ECE_DEPT", "ECE Department", "192.168.2.10", "Department Host", "ONLINE", 9, "📡", "ECE IoT & Embedded Systems Lab", List.of("SW_ECE"));
        addNode("LIBRARY_DEPT", "Library", "192.168.3.10", "Department Host", "ONLINE", 12, "📚", "Digital Library & Catalogue PCs", List.of("SW_LIB"));
        addNode("ADMIN_DEPT", "Administrative Office", "192.168.4.10", "Department Host", "ONLINE", 7, "🏢", "Registrar & Examination System", List.of("SW_ADMIN"));
        addNode("SERVER_ROOM", "Server Room", "192.168.5.10", "Campus Server", "ONLINE", 4, "🗄️", "Central Campus ERP & Web Portal", List.of("SW_SERVER"));

        // 2. Network Infrastructure
        addNode("CORE_ROUTER", "Core Router", "192.168.0.1", "Core Router", "ONLINE", 3, "🌐", "Campus Backbone Gateway Router", List.of("GATEWAY", "CORE_SWITCH"));
        addNode("CORE_SWITCH", "Core Switch", "192.168.0.2", "Core Switch", "ONLINE", 2, "🔀", "High-capacity Multi-layer Core Switch", List.of("CORE_ROUTER", "SW_CS", "SW_ECE", "SW_LIB", "SW_ADMIN", "SW_SERVER"));
        addNode("SW_CS", "CS Access Switch", "192.168.1.1", "Access Switch", "ONLINE", 4, "🔌", "CS Dept Gigabit Distribution Switch", List.of("CORE_SWITCH", "CS_DEPT", "SW_ECE", "SW_SERVER"));
        addNode("SW_ECE", "ECE Access Switch", "192.168.2.1", "Access Switch", "ONLINE", 5, "🔌", "ECE Dept Gigabit Distribution Switch", List.of("CORE_SWITCH", "ECE_DEPT", "SW_CS"));
        addNode("SW_LIB", "Library Switch", "192.168.3.1", "Access Switch", "ONLINE", 6, "🔌", "Library LAN Distribution Switch", List.of("CORE_SWITCH", "LIBRARY_DEPT", "SW_ADMIN"));
        addNode("SW_ADMIN", "Admin Switch", "192.168.4.1", "Access Switch", "ONLINE", 4, "🔌", "Admin Block Distribution Switch", List.of("CORE_SWITCH", "ADMIN_DEPT", "SW_LIB"));
        addNode("SW_SERVER", "Server Switch", "192.168.5.1", "Access Switch", "ONLINE", 2, "🔌", "High-Speed Data Center Switch", List.of("CORE_SWITCH", "SERVER_ROOM", "SW_CS"));
        addNode("GATEWAY", "Internet Gateway", "10.0.0.1", "Internet Gateway", "ONLINE", 15, "🌍", "ISP Border Connection & Firewall", List.of("CORE_ROUTER"));

        // 3. Edges (Logical connections with positive metrics/costs)
        addEdge("GATEWAY", "CORE_ROUTER", 5, "ISP Fiber Link (5 ms)");
        addEdge("CORE_ROUTER", "CORE_SWITCH", 2, "10GbE Backbone Trunk (2 ms)");

        addEdge("CORE_SWITCH", "SW_CS", 3, "Gigabit Fiber Link (3 ms)");
        addEdge("CORE_SWITCH", "SW_ECE", 4, "Gigabit Fiber Link (4 ms)");
        addEdge("CORE_SWITCH", "SW_LIB", 3, "Gigabit Fiber Link (3 ms)");
        addEdge("CORE_SWITCH", "SW_ADMIN", 2, "Gigabit Fiber Link (2 ms)");
        addEdge("CORE_SWITCH", "SW_SERVER", 1, "Direct 10GbE Data Center Trunk (1 ms)");

        // End-host to switch links
        addEdge("SW_CS", "CS_DEPT", 1, "Fast Ethernet Local Access (1 ms)");
        addEdge("SW_ECE", "ECE_DEPT", 1, "Fast Ethernet Local Access (1 ms)");
        addEdge("SW_LIB", "LIBRARY_DEPT", 1, "Fast Ethernet Local Access (1 ms)");
        addEdge("SW_ADMIN", "ADMIN_DEPT", 1, "Fast Ethernet Local Access (1 ms)");
        addEdge("SW_SERVER", "SERVER_ROOM", 1, "High-Speed Server Link (1 ms)");

        // Redundant / Inter-department mesh cross-links
        addEdge("SW_CS", "SW_ECE", 3, "Academic Cross-Link (3 ms)");
        addEdge("SW_LIB", "SW_ADMIN", 2, "Admin-Library Interconnect (2 ms)");
        addEdge("SW_CS", "SW_SERVER", 4, "Lab-Server Direct Backup Trunk (4 ms)");

        // 4. Initialize Devices for monitoring table
        for (TopologyNode node : nodeMap.values()) {
            deviceMap.put(node.getId(), new Device(
                    node.getId(),
                    node.getName(),
                    node.getIpAddress(),
                    node.getType(),
                    node.getStatus(),
                    node.getLatencyMs(),
                    node.getName()
            ));
        }
    }

    private void addNode(String id, String name, String ip, String type, String status, int latency, String icon, String desc, List<String> connected) {
        TopologyNode node = new TopologyNode(id, name, ip, type, status, latency, icon, desc, connected);
        nodeMap.put(id, node);
    }

    private void addEdge(String from, String to, int cost, String desc) {
        edges.add(new TopologyEdge(from, to, cost, desc));
    }

    public NetworkTopology getTopology() {
        return new NetworkTopology(new ArrayList<>(nodeMap.values()), new ArrayList<>(edges));
    }

    public Map<String, TopologyNode> getNodeMap() {
        return Collections.unmodifiableMap(nodeMap);
    }

    public List<TopologyEdge> getEdges() {
        return Collections.unmodifiableList(edges);
    }

    public List<Device> getAllDevices() {
        return new ArrayList<>(deviceMap.values());
    }

    public Optional<Device> toggleDeviceStatus(String id) {
        Device device = deviceMap.get(id);
        if (device != null) {
            String newStatus = "ONLINE".equalsIgnoreCase(device.getStatus()) ? "OFFLINE" : "ONLINE";
            device.setStatus(newStatus);
            if ("OFFLINE".equalsIgnoreCase(newStatus)) {
                device.setLatencyMs(-1);
            } else {
                TopologyNode node = nodeMap.get(id);
                device.setLatencyMs(node != null ? node.getLatencyMs() : 5);
            }
            // Also update node
            TopologyNode node = nodeMap.get(id);
            if (node != null) {
                node.setStatus(newStatus);
            }
            return Optional.of(device);
        }
        return Optional.empty();
    }
}
