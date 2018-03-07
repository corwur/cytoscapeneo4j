package nl.corwur.cytoscape.neo4j.internal.graph;

public interface GraphVisitor {
    void visit(GraphNode graphNode);
    void visit(GraphEdge graphEdge);
    void visit(GraphResult neo4jResult);
    void visit(GraphLong neo4jLong);
    void visit(GraphUnspecifiedType neo4jUnspecifiedType);
    void visit(GraphPath graphPath);
    void visit(GraphObjectList graphObjectList);
}
