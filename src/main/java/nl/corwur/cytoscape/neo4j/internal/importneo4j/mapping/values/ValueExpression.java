package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;

public interface ValueExpression<T,V> {
    V eval(T val);
    void accept(ValueExpressionVisitor visitor);
}
