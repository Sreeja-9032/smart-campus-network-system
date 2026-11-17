package com.smartcampus.network.model;

public class RouteHop {
    private String fromNode;
    private String fromName;
    private String toNode;
    private String toName;
    private int cost;
    private int cumulativeCost;

    public RouteHop() {}

    public RouteHop(String fromNode, String fromName, String toNode, String toName, int cost, int cumulativeCost) {
        this.fromNode = fromNode;
        this.fromName = fromName;
        this.toNode = toNode;
        this.toName = toName;
        this.cost = cost;
        this.cumulativeCost = cumulativeCost;
    }

    public String getFromNode() { return fromNode; }
    public void setFromNode(String fromNode) { this.fromNode = fromNode; }

    public String getFromName() { return fromName; }
    public void setFromName(String fromName) { this.fromName = fromName; }

    public String getToNode() { return toNode; }
    public void setToNode(String toNode) { this.toNode = toNode; }

    public String getToName() { return toName; }
    public void setToName(String toName) { this.toName = toName; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getCumulativeCost() { return cumulativeCost; }
    public void setCumulativeCost(int cumulativeCost) { this.cumulativeCost = cumulativeCost; }
}
