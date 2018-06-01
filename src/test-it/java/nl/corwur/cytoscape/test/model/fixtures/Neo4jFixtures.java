package nl.corwur.cytoscape.test.model.fixtures;

import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.PropertyKey;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jGraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Neo4jFixtures {


    private final GraphImplementation gi;
    private final Neo4jClient neo4jClient;


    public Neo4jFixtures(Neo4jClient neo4jClient, String networkLabel) {
        this.neo4jClient = neo4jClient;
        this.gi = Neo4jGraphImplementation.create(neo4jClient, TaskConstants.NEO4J_PROPERTY_CYTOSCAPE_NETWORK, networkLabel);
    }
    interface CreateGraph {
        void create(Neo4jClient neo4jClient, String networkLabel) throws GraphImplementationException;
    }

    public enum Neo4jFixture {
        GRAPH_5_STAR(Neo4jFixtures::createFiveStarGraph);

        private final CreateGraph createGraph;

        Neo4jFixture(CreateGraph createGraph) {
            this.createGraph = createGraph;
        }

        public void create(Neo4jClient neo4jClient, String networkLabel) throws GraphImplementationException {
            createGraph.create(neo4jClient, networkLabel);
        }

    }

    private static void createFiveStarGraph(Neo4jClient neo4jClient, String networkLabel) throws GraphImplementationException {
        new Neo4jFixtures(neo4jClient, networkLabel).createStarGraph(5);
    }

    private void createStarGraph(int n) throws GraphImplementationException {
        for(int i = 0; i < n; i++) {
            createNode(i);
        }
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(i != j) {
                    createEdge(i,j);
                }
            }
        }
    }

    private void createNode(int i) throws GraphImplementationException {
        Map<String,Object> props = new HashMap<>();
        props.put("nodeId", i);
        gi.addNode(Collections.emptyList(), props);
    }

    private void createEdge(int start, int end) throws GraphImplementationException {
        gi.addEdge(new PropertyKey("nodeId", start), new PropertyKey("nodeId", end), "REL_" + start + "_" + end, Collections.emptyMap());
    }

}
