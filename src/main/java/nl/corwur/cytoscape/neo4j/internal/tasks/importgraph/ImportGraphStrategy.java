package nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import org.cytoscape.model.CyNetwork;

/**
 * This interface specifies an import strategy for copying nodes and edges from Neo4j to Cytoscape.
 */
public interface ImportGraphStrategy {
    void createTables(CyNetwork network);
    default void copyGraph(CyNetwork network, Graph graph) {
        graph.nodes().forEach(node -> copyNode(network, node));
        graph.edges().forEach(edge -> copyEdge(network, edge));
    }
    void copyNode(CyNetwork network, GraphNode graphNode);
    void copyEdge(CyNetwork network, GraphEdge graphEdge);
    void postProcess(CyNetwork network);
}
