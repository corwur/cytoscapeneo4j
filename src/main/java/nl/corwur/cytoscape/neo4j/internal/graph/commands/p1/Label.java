package nl.corwur.cytoscape.neo4j.internal.graph.commands.p1;

public abstract class Label {
    private final String label;
    public Label(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
