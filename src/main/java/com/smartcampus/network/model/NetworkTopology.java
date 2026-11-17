package com.smartcampus.network.model;

import java.util.List;

public class NetworkTopology {
    private List<TopologyNode> nodes;
    private List<TopologyEdge> edges;

    public NetworkTopology() {}

    public NetworkTopology(List<TopologyNode> nodes, List<TopologyEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public List<TopologyNode> getNodes() { return nodes; }
    public void setNodes(List<TopologyNode> nodes) { this.nodes = nodes; }

    public List<TopologyEdge> getEdges() { return edges; }
    public void setEdges(List<TopologyEdge> edges) { this.edges = edges; }
}
