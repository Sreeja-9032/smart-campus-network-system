package com.smartcampus.network.model;

public class Device {
    private String id;
    private String name;
    private String ipAddress;
    private String type;
    private String status; // Online, Offline
    private int latencyMs;
    private String department;
    private boolean simulated;

    public Device() {
        this.simulated = true;
    }

    public Device(String id, String name, String ipAddress, String type, String status, int latencyMs, String department) {
        this.id = id;
        this.name = name;
        this.ipAddress = ipAddress;
        this.type = type;
        this.status = status;
        this.latencyMs = latencyMs;
        this.department = department;
        this.simulated = true;
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

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public boolean isSimulated() { return simulated; }
    public void setSimulated(boolean simulated) { this.simulated = simulated; }
}
