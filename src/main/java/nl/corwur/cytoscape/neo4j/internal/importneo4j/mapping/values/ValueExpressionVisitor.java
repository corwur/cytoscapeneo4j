package nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.values;

public interface ValueExpressionVisitor {
    void visit(NodeId nodeId);
    void visit(EdgeId edgeId);
    <T> void visit(EdgePropertyValue<T> edgePropertyValue);
    void visit(EdgeScriptExpression vEdgeScriptExpression);
    void visit(LabelValue labelValue);
    <T> void visit(NodePropertyValue<T> tNodePropertyValue);
    <T> void visit(NodeScriptExpression<T> tNodeScriptExpression);
}
