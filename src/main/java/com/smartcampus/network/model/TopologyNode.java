package com.smartcampus.network.model;

import java.util.List;

public class TopologyNode {
    private String id;
    private String name;
    private String ipAddress;
    private String type; // Department Host, Core Router, Core Switch, Access Switch, Internet Gateway
    private String status; // ONLINE, OFFLINE
    private int latencyMs;
    private String icon;
    private String description;
    private List<String> connectedNodes;

    public TopologyNode() {}

    public TopologyNode(String id, String name, String ipAddress, String type, String status, int latencyMs, String icon, String description, List<String> connectedNodes) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.type = type;
        this.status = status;
        this.latencyMs = latencyMs;
        this.icon = icon;
        this.description = description;
        this.connectedNodes = connectedNodes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getLatencyMs() { return latencyMs; }
    public void setLatencyMs(int latencyMs) { this.latencyMs = latencyMs; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getConnectedNodes() { return connectedNodes; }
    public void setConnectedNodes(List<String> connectedNodes) { this.connectedNodes = connectedNodes; }
}
