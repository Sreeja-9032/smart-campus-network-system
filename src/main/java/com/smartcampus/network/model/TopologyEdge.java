package com.smartcampus.network.model;

public class TopologyEdge {
    private String from;
    private String to;
    private int cost;
    private String description;

    public TopologyEdge() {}

    public TopologyEdge(String from, String to, int cost, String description) {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.description = description;
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
