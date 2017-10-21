package nl.corwur.cytoscape.neo4j.internal.graph;

public class GraphUnspecifiedType extends GraphObject {
    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }
}
