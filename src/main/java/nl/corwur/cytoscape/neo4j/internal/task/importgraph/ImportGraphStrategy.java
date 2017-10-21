package nl.corwur.cytoscape.neo4j.internal.task.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import org.cytoscape.model.CyNetwork;

public interface ImportGraphStrategy {
    void createTables(CyNetwork network);
    void handleNode(CyNetwork network, GraphNode graphNode);
    void handleEdge(CyNetwork network, GraphEdge graphEdge);
}
