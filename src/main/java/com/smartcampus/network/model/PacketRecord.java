package com.smartcampus.network.model;

public class PacketRecord {
    private int packetNumber;
    private double timestamp;
    private String sourceIp;
    private String destinationIp;
    private String protocol; // TCP, UDP, ICMP, HTTP, DNS, ARP
    private int lengthBytes;
    private String status;
    private String info;
    private String origin; // SIMULATED, IMPORTED_WIRESHARK

    public PacketRecord() {
        this.origin = "SIMULATED";
    }

    public PacketRecord(int packetNumber, double timestamp, String sourceIp, String destinationIp, String protocol, int lengthBytes, String status, String info, String origin) {
        this.packetNumber = packetNumber;
        this.timestamp = timestamp;
        this.sourceIp = sourceIp;
        this.destinationIp = destinationIp;
        this.protocol = protocol;
        this.lengthBytes = lengthBytes;
        this.status = status;
        this.info = info;
        this.origin = origin;
    }

    public int getPacketNumber() { return packetNumber; }
    public void setPacketNumber(int packetNumber) { this.packetNumber = packetNumber; }

    public double getTimestamp() { return timestamp; }
    public void setTimestamp(double timestamp) { this.timestamp = timestamp; }

    public String getSourceIp() { return sourceIp; }
    public void setSourceIp(String sourceIp) { this.sourceIp = sourceIp; }

    public String getDestinationIp() { return destinationIp; }
    public void setDestinationIp(String destinationIp) { this.destinationIp = destinationIp; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public int getLengthBytes() { return lengthBytes; }
    public void setLengthBytes(int lengthBytes) { this.lengthBytes = lengthBytes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }
}
