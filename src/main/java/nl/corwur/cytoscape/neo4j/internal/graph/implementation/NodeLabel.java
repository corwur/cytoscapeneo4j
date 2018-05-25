package nl.corwur.cytoscape.neo4j.internal.graph.implementation;


public class NodeLabel extends Label {

    private NodeLabel(String label) {
        super(label);
    }

    public static NodeLabel create(String label) {
        if (label.matches("[\\w\\d\\s]+")) {
            return new NodeLabel(label);
        }
        throw new IllegalStateException();
    }

}
