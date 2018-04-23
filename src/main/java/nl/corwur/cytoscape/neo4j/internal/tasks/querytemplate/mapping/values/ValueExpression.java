package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.values;

/**
 * This interface specifies a value expression that can be evaluated. It is used to map Neo4j nodes and edges to Cytoscape.
 * @param <T>
 * @param <V>
 */
public interface ValueExpression<T,V> {
    V eval(T val);
    void accept(ValueExpressionVisitor visitor);
}
