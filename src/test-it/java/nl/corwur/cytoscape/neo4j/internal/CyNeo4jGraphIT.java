package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.Command;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.CommandException;
import nl.corwur.cytoscape.neo4j.internal.neo4j.*;
import nl.corwur.cytoscape.neo4j.internal.tasks.ImportAllNodesAndEdgesFromNeo4JTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportDifference;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNew;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import org.cytoscape.model.CyNetwork;
import org.junit.*;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.UUID;

import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.Fixture.NETWORK_WITH_3_NODES_1_EDGE;
import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.emptyNetwork;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
    public void testExportDiffrence() throws CommandException, Neo4jClientException {
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
        ExportDifference exportDifference1 = ExportDifference.create(graph, cyNetwork, graphImplementation);
        Command command1 = exportDifference1.compute();
        command1.execute();

        //import graph
        Graph importGraph1 = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));

        assertEquals(3, importGraph1.nodes().size());
        assertEquals(1, importGraph1.edges().size());



    }

    private String randomLabel() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
    }

    private CypherQuery importAllNodesAndEdges(String networkLabel) {
        String query = MessageFormat.format(ImportAllNodesAndEdgesFromNeo4JTask.MATCH_ALL_NODES_AND_EDGES, networkLabel);
        return CypherQuery.builder().query(query).build();
    }

}
