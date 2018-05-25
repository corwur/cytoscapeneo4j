package nl.corwur.cytoscape.neo4j.internal.graph;

/**
 * This interface specifies a graph visitor that can be used for processing graph objects.
 */
public interface GraphVisitor {
    void visit(GraphNode graphNode);

    void visit(GraphEdge graphEdge);

    void visit(GraphResult neo4jResult);

    void visit(GraphLong neo4jLong);

    void visit(GraphUnspecifiedType neo4jUnspecifiedType);

    void visit(GraphPath graphPath);

    void visit(GraphObjectList graphObjectList);

    void visit(Graph graph);
}
