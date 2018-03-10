package nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import org.cytoscape.model.CyNetwork;

/**
 * This interface specifies an import strategy for copying nodes and edges from Neo4j to Cytoscape.
 */
public interface ImportGraphStrategy {
    void createTables(CyNetwork network);
    void handleNode(CyNetwork network, GraphNode graphNode);
    void handleEdge(CyNetwork network, GraphEdge graphEdge);
}
