package nl.corwur.cytoscape.neo4j.internal.graph;

/**
 * This class represents a long value in a graph.
 */
public class GraphLong implements GraphObject {

    private final long value;

    public GraphLong(long value) {
        this.value = value;
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    public long getValue() {
        return value;
    }
}
