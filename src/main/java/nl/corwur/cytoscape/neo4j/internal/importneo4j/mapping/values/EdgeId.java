package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;

public class EdgeId implements ValueExpression<GraphEdge,Long> {
    @Override
    public Long eval(GraphEdge val) {
        return val.getId();
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
