package com.smartcampus.network.model;

import java.util.List;

public class DijkstraResponse {
    private boolean success;
    private String message;
    private String source;
    private String destination;
    private String sourceName;
    private String destinationName;
    private int totalCost;
    private List<String> pathNodeIds;
    private List<String> pathNodeNames;
    private List<String> intermediateDevices;
    private List<RouteHop> hops;

    public DijkstraResponse() {}

    public static DijkstraResponse error(String message) {
        DijkstraResponse resp = new DijkstraResponse();
        resp.setSuccess(false);
        resp.setMessage(message);
        return resp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }

    public int getTotalCost() { return totalCost; }
    public void setTotalCost(int totalCost) { this.totalCost = totalCost; }

    public List<String> getPathNodeIds() { return pathNodeIds; }
    public void setPathNodeIds(List<String> pathNodeIds) { this.pathNodeIds = pathNodeIds; }

    public List<String> getPathNodeNames() { return pathNodeNames; }
    public void setPathNodeNames(List<String> pathNodeNames) { this.pathNodeNames = pathNodeNames; }

    public List<String> getIntermediateDevices() { return intermediateDevices; }
    public void setIntermediateDevices(List<String> intermediateDevices) { this.intermediateDevices = intermediateDevices; }

    public List<RouteHop> getHops() { return hops; }
    public void setHops(List<RouteHop> hops) { this.hops = hops; }
}
