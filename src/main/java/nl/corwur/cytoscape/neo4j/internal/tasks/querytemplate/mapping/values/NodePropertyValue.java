package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping.values;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;

/**
 * This class implements the value expression for a node property.
 *
 * @param <T>
 */
public class NodePropertyValue<T> implements ValueExpression<GraphNode, T> {
    private final String key;
    private final Class<T> type;

    public NodePropertyValue(String key, Class<T> type) {
        this.key = key;
        this.type = type;
    }

    @Override
    public T eval(GraphNode val) {
        return val.getProperty(key, type).orElse(null);
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public String getKey() {
        return key;
    }

    public Class<T> getType() {
        return type;
    }
}
