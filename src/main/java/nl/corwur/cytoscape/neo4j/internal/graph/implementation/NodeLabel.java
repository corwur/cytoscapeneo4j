package nl.corwur.cytoscape.neo4j.internal.graph.implementation;


public class NodeLabel extends Label {

    private NodeLabel(String label) {
        super(label);
    }

    public static NodeLabel create(String label) {
        return new NodeLabel(label);
    }
}
