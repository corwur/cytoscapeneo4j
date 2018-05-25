package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.Command;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.CommandException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.neo4j.ConnectionParameter;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jGraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.tasks.ImportAllNodesAndEdgesFromNeo4JTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportDifference;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNew;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import nl.corwur.cytoscape.test.model.fixtures.Neo4jFixtures;
import org.cytoscape.model.CyNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.UUID;

import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.CyFixture.NETWORK_WITH_3_NODES_1_EDGE;
import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.emptyNetwork;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@Ignore
//@RunWith(MockitoJUnitRunner.class)
public class CyNeo4jGraphIT {

    private String neo4jUrl = System.getenv("CYNEO4J_TEST_NEO4J_URL");
    private String user = System.getenv("CYNEO4J_TEST_NEO4J_USER");
    private String passwd = System.getenv("CYNEO4J_TEST_NEO4J_PASS");

    private Neo4jClient neo4jClient = new Neo4jClient();

    @Before
    public void setUp() {
        assertNotNull("neo4j url not set", neo4jUrl);
        assertNotNull("neo4j user not set", user);
        assertNotNull("neo4j password not set", passwd);
        assertTrue("could not connect to neo4j", neo4jClient.connect(new ConnectionParameter(neo4jUrl, user, passwd.toCharArray())));
    }
    @After
    public void tearDown() {
        neo4jClient.close();
    }

    @Test
    public void testExportNew() throws CommandException, Neo4jClientException {
        String networkLabel = randomLabel();
        Neo4jGraphImplementation graphImplementation = Neo4jGraphImplementation.create(neo4jClient, networkLabel);

        CyNetwork cyNetwork = NETWORK_WITH_3_NODES_1_EDGE.getNetwork();
        ExportNew exportNew = ExportNew.create(cyNetwork, graphImplementation);
        exportNew.compute().execute();

        Graph importGraph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));
        assertEquals(3, importGraph.nodes().size());
        assertEquals(1, importGraph.edges().size());
    }

    @Test
    public void testExportDifference() throws CommandException, Neo4jClientException {
        String networkLabel = randomLabel();
        Neo4jGraphImplementation graphImplementation = Neo4jGraphImplementation.create(neo4jClient, networkLabel);

        //export new graph
        ExportNew.create(NETWORK_WITH_3_NODES_1_EDGE.getNetwork(), graphImplementation).compute().execute();

        //import graph
        Graph graph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));
        CyNetwork cyNetwork = emptyNetwork();
        ImportGraphToCytoscape importGraphToCytoscape = new ImportGraphToCytoscape(cyNetwork, new DefaultImportStrategy(), () -> false);
        importGraphToCytoscape.importGraph(graph);

        //export difference
        ExportDifference exportDifference = ExportDifference.create(graph, cyNetwork, graphImplementation);
        Command command = exportDifference.compute();
        command.execute();

        //import graph
        Graph importGraph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));

        assertEquals(3, importGraph.nodes().size());
        assertEquals(1, importGraph.edges().size());
    }

    @Test
    public void testExportDifferenceWithEdges() throws CommandException, Neo4jClientException, GraphImplementationException {
        String networkLabel = randomLabel();


        Neo4jGraphImplementation graphImplementation = Neo4jGraphImplementation.create(neo4jClient, networkLabel);
        Neo4jFixtures.Neo4jFixture.GRAPH_5_STAR.create(neo4jClient, networkLabel);

        //import graph
        Graph graph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));
        CyNetwork cyNetwork = emptyNetwork();
        ImportGraphToCytoscape importGraphToCytoscape = new ImportGraphToCytoscape(cyNetwork, new DefaultImportStrategy(), () -> false);
        importGraphToCytoscape.importGraph(graph);

        //export difference
        ExportDifference exportDifference = ExportDifference.create(graph, cyNetwork, graphImplementation);
        Command command = exportDifference.compute();
        command.execute();

        //import graph
        Graph importGraph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));

        assertEquals(5, importGraph.nodes().size());
        assertEquals(20, importGraph.edges().size());
    }





    private String randomLabel() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    private CypherQuery importAllNodesAndEdges(String networkLabel) {
        String query = MessageFormat.format(ImportAllNodesAndEdgesFromNeo4JTask.MATCH_ALL_NODES_AND_EDGES, networkLabel);
        return CypherQuery.builder().query(query).build();
    }

}
