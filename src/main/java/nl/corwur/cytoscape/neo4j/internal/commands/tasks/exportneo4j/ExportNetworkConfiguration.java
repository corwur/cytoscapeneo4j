package nl.corwur.cytoscape.neo4j.internal.commands.tasks.exportneo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.NodeLabel;

public class ExportNetworkConfiguration {
    private final NodeLabel nodeLabel;
    private final String relationship;

    public ExportNetworkConfiguration(NodeLabel nodeLabel, String relationship) {
        this.nodeLabel = nodeLabel;
        this.relationship = relationship;
    }

    public NodeLabel getNodeLabel() {
        return nodeLabel;
    }

    public String getRelationship() {
        return relationship;
    }


    public static ExportNetworkConfiguration create(NodeLabel nodeLabel, String relationship) {
        return new ExportNetworkConfiguration(nodeLabel, relationship);
    }
}
