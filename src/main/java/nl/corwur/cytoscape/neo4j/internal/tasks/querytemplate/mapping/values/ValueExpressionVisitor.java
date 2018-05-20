package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping.values;

/**
 * This interface specifies a value expression visitor.
 * It is used to traverse the mapping from Neo4j to Cytoscape.
 */
public interface ValueExpressionVisitor {
    void visit(NodeId nodeId);
    void visit(EdgeId edgeId);
    <T> void visit(EdgePropertyValue<T> edgePropertyValue);
    void visit(EdgeScriptExpression vEdgeScriptExpression);
    void visit(LabelValue labelValue);
    <T> void visit(NodePropertyValue<T> tNodePropertyValue);
    <T> void visit(NodeScriptExpression<T> tNodeScriptExpression);
}
