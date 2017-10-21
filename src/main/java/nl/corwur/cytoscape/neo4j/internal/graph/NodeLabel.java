package nl.corwur.cytoscape.neo4j.internal.graph;

public class NodeLabel {
    private final String label;
    private NodeLabel(String label) {
        this.label = label;
    }
    public static NodeLabel create(String label) {
        if(label.matches("[\\w\\d]+")) {
            return new NodeLabel(label);
        }
        throw new IllegalStateException();
     }

    public String getLabel() {
        return label;
    }
}
