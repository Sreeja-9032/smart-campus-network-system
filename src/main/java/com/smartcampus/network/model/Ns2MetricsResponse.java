package com.smartcampus.network.model;

import java.util.List;
import java.util.Map;

public class Ns2MetricsResponse {
    private boolean success;
    private String message;
    private String mode; // "DEMONSTRATION" or "IMPORTED_TRACE"
    private int sentPackets;
    private int receivedPackets;
    private int droppedPackets;
    private double packetLossPercentage;
    private double packetDeliveryRatioPercentage;
    private double throughputMbps;
    private double averageDelayMs;
    private double simulationDurationSeconds;
    private Map<String, String> formulaExplanations;
    private List<Double> throughputTimePoints;
    private List<String> timeLabels;

    public Ns2MetricsResponse() {}

    public static Ns2MetricsResponse error(String message) {
        Ns2MetricsResponse resp = new Ns2MetricsResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public int getSentPackets() { return sentPackets; }
    public void setSentPackets(int sentPackets) { this.sentPackets = sentPackets; }

    public int getReceivedPackets() { return receivedPackets; }
    public void setReceivedPackets(int receivedPackets) { this.receivedPackets = receivedPackets; }

    public int getDroppedPackets() { return droppedPackets; }
    public void setDroppedPackets(int droppedPackets) { this.droppedPackets = droppedPackets; }

    public double getPacketLossPercentage() { return packetLossPercentage; }
    public void setPacketLossPercentage(double packetLossPercentage) { this.packetLossPercentage = packetLossPercentage; }

    public double getPacketDeliveryRatioPercentage() { return packetDeliveryRatioPercentage; }
    public void setPacketDeliveryRatioPercentage(double packetDeliveryRatioPercentage) { this.packetDeliveryRatioPercentage = packetDeliveryRatioPercentage; }

    public double getThroughputMbps() { return throughputMbps; }
    public void setThroughputMbps(double throughputMbps) { this.throughputMbps = throughputMbps; }

    public double getAverageDelayMs() { return averageDelayMs; }
    public void setAverageDelayMs(double averageDelayMs) { this.averageDelayMs = averageDelayMs; }

    public double getSimulationDurationSeconds() { return simulationDurationSeconds; }
    public void setSimulationDurationSeconds(double simulationDurationSeconds) { this.simulationDurationSeconds = simulationDurationSeconds; }

    public Map<String, String> getFormulaExplanations() { return formulaExplanations; }
    public void setFormulaExplanations(Map<String, String> formulaExplanations) { this.formulaExplanations = formulaExplanations; }

    public List<Double> getThroughputTimePoints() { return throughputTimePoints; }
    public void setThroughputTimePoints(List<Double> throughputTimePoints) { this.throughputTimePoints = throughputTimePoints; }

    public List<String> getTimeLabels() { return timeLabels; }
    public void setTimeLabels(List<String> timeLabels) { this.timeLabels = timeLabels; }
}
