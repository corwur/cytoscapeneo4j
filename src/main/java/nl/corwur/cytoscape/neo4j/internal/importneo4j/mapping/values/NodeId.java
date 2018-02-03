package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;

public class NodeId implements ValueExpression<GraphNode,Long> {
    @Override
    public Long eval(GraphNode val) {
        return val.getId();
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
