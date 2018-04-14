package nl.corwur.cytoscape.neo4j.internal.graph;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class GraphTest {

    @Test
    public void addNode() {
        Graph graph = new Graph();
        graph.add(new GraphNode(1));
        graph.add(new GraphNode(2));
        assert(graph.size() == 2);

        GraphNode node1 = new GraphNode(1);
        node1.getProperties().put("test", 123);
        graph.add(node1);
        assert(graph.size() == 2);

    }

    @Test
    public void add1() {
    }
}