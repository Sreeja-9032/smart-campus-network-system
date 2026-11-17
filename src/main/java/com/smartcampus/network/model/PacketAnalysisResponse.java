package com.smartcampus.network.model;

import java.util.List;
import java.util.Map;

public class PacketAnalysisResponse {
    private boolean success;
    private String message;
    private String dataSource; // "Simulated Campus Traffic" or "Imported Wireshark CSV Capture"
    private int totalPackets;
    private Map<String, Integer> protocolBreakdown;
    private List<PacketRecord> packets;

    public PacketAnalysisResponse() {}

    public static PacketAnalysisResponse error(String message) {
        PacketAnalysisResponse resp = new PacketAnalysisResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public int getTotalPackets() { return totalPackets; }
    public void setTotalPackets(int totalPackets) { this.totalPackets = totalPackets; }

    public Map<String, Integer> getProtocolBreakdown() { return protocolBreakdown; }
    public void setProtocolBreakdown(Map<String, Integer> protocolBreakdown) { this.protocolBreakdown = protocolBreakdown; }

    public List<PacketRecord> getPackets() { return packets; }
    public void setPackets(List<PacketRecord> packets) { this.packets = packets; }
}
