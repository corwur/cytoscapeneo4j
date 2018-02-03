package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;

public class NodeScriptExpression<T> extends ValueScriptExpression<GraphNode, T> {

    public NodeScriptExpression(String script, Class<T> type) {
        super(script, "node", type);
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }
}
