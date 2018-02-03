package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;

public class EdgeScriptExpression<V> extends ValueScriptExpression<GraphEdge, V> {

    public EdgeScriptExpression(String script, Class<V> type) {
        super(script, "edge", type);
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
