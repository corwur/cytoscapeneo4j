package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.Command;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.CommandException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.neo4j.ConnectionParameter;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jGraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskConstants;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportDifference;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNew;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures;
import nl.corwur.cytoscape.test.model.fixtures.Neo4jFixtures;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.CyFixture.NETWORK_WITH_3_NODES_1_EDGE;
import static nl.corwur.cytoscape.test.model.fixtures.CyNetworkFixtures.emptyNetwork;
import static nl.corwur.cytoscape.test.model.fixtures.Neo4jFixtures.Neo4jFixture.GRAPH_5_STAR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

        Steps.newInstance(neo4jClient)
                .givenRandomNetwork()
                .whenExportGraph(NETWORK_WITH_3_NODES_1_EDGE)
                .whenImportGraph()
                .thenGraphHas("All nodes should be imported", 3, Steps.Types.NODES)
                .thenGraphHas("All edges should be imported", 1, Steps.Types.EDGES)
                .thenGraphEdgesAllMatch("Edge does not have name AB", this::oneEdgeWithNameAB)
                .thenGraphNodesAllMatch("All nodes must have a name", this::nodeHasNonEmptyName)
                .thenGraphNodesAllMatch("All nodes must have at exactly one label", this::nodeHasOneLabel)
                .thenGraphNodesAllMatch("The node name must equals the node label", this::nodeNameEqualsLabel);
    }

    @Test
    public void testExportDifference() throws CommandException, Neo4jClientException {
        //Given network label
        Steps.newInstance(neo4jClient)
                .givenRandomNetwork()
                .whenExportGraph(NETWORK_WITH_3_NODES_1_EDGE)
                .whenImportGraph()
                .whenAddNode()
                .whenAddEdge(propertyExists("name", "b"), propertyExists("name", "c"))
                .whenExportDifference()
                .whenImportGraph()
                .thenNetworkHas(4, Steps.Types.NODES)
                .thenNetworkHas(2, Steps.Types.EDGES);
    }

    @Test
    public void testImport5StarGraph() throws Neo4jClientException, GraphImplementationException {
        Steps.newInstance(neo4jClient)
                .givenRandomNetwork()
                .whenCreateNeo4jGraph(GRAPH_5_STAR)
                .whenImportGraph()
                .thenNetworkHas(5, Steps.Types.NODES)
                .thenNetworkHas(20, Steps.Types.EDGES);

    }

    private BiPredicate<CyNetwork, CyNode> propertyExists(String key, String value) {
        return (network, node) ->
                network.getRow(node).isSet(key) &&
                        network.getRow(node).get(key, String.class).equals(value);
    }

    private boolean oneEdgeWithNameAB(GraphEdge edge) {
        return edge.getProperty("name", String.class).filter(name -> "AB".equals(name)).isPresent();
    }

    private boolean nodeNameEqualsLabel(GraphNode node) {
        return node.getProperty("name", String.class).filter(name -> name.equals(node.getLabels().get(0))).isPresent();
    }

    private boolean nodeHasOneLabel(GraphNode node) {
        return node.getLabels().size() == 1;
    }

    private boolean nodeHasNonEmptyName(GraphNode node) {
        return node.getProperty("name", String.class).filter(name -> !name.isEmpty()).isPresent();
    }

    private static final class Steps {


        public Steps(Neo4jClient neo4jClient) {
            this.neo4jClient = neo4jClient;
        }

        public Steps thenGraphEdgesAllMatch(String message, Predicate<GraphEdge> predicate) {
            assertTrue(message, graph.edges().stream().allMatch(predicate));
            return this;
        }

        public Steps thenGraphNodesAllMatch(String message, Predicate<GraphNode> predicate) {
            assertTrue(message, graph.nodes().stream().allMatch(predicate));
            return this;
        }

        public Steps whenCreateNeo4jGraph(Neo4jFixtures.Neo4jFixture neo4jFixture) throws GraphImplementationException {
            neo4jFixture.create(neo4jClient, networkLabel);
            return this;
        }

        public enum Types {
            NODES, EDGES;


        }

        private String networkLabel;
        private Neo4jClient neo4jClient;
        private Neo4jGraphImplementation graphImplementation;
        private Graph graph;
        private CyNetwork cyNetwork;
        private GraphNode node;
        private GraphEdge edge;


        public static Steps newInstance(Neo4jClient neo4jClient) {
            return new Steps(neo4jClient);
        }

        public Steps givenRandomNetwork() {
            this.networkLabel = randomLabel();
            graphImplementation = Neo4jGraphImplementation.create(neo4jClient, TaskConstants.NEO4J_PROPERTY_CYTOSCAPE_NETWORK, networkLabel);
            return this;
        }

        public Steps whenExportGraph(CyNetworkFixtures.CyFixture cyNetworkFixture) throws CommandException {
            ExportNew.create(cyNetworkFixture.getNetwork(), graphImplementation).compute().execute();
            return this;
        }

        public Steps whenImportGraph() throws Neo4jClientException {
            this.graph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));
            this.cyNetwork = emptyNetwork();
            ImportGraphToCytoscape importGraphToCytoscape = new ImportGraphToCytoscape(cyNetwork, new DefaultImportStrategy(), () -> false);
            importGraphToCytoscape.importGraph(graph);
            return this;
        }

        public Steps whenExportDifference() throws CommandException, Neo4jClientException {
            Graph neo4jGraph = neo4jClient.getGraph(importAllNodesAndEdges(networkLabel));
            ExportDifference exportDifference = ExportDifference.create(neo4jGraph, cyNetwork, graphImplementation);
            Command command = exportDifference.compute();
            command.execute();
            return this;
        }

        public Steps whenAddNode() {
            CyNode cyNode = cyNetwork.addNode();
            return this;
        }

        public Steps whenAddEdge(BiPredicate<CyNetwork, CyNode> selectSource, BiPredicate<CyNetwork, CyNode> selectTarget) {
            Predicate<CyNode> sourcePredicate = cyNode -> selectSource.test(cyNetwork, cyNode);
            Predicate<CyNode> targetPredicate = cyNode -> selectTarget.test(cyNetwork, cyNode);
            CyNode source = cyNetwork.getNodeList().stream().filter(sourcePredicate).findFirst().orElseThrow(() -> new IllegalArgumentException("CyNode not found"));
            CyNode target = cyNetwork.getNodeList().stream().filter(targetPredicate).findFirst().orElseThrow(() -> new IllegalArgumentException("CyNode not found"));
            cyNetwork.addEdge(source, target, true);
            return this;
        }

        public Steps thenGraphHas(int expected, Types types) {
            thenGraphHas("Unexpected number of " + types.name(), expected, types);
            return this;
        }

        public Steps thenGraphHas(String message, int expected, Types types) {
            switch (types) {
                case NODES:
                    assertEquals(message, expected, graph.nodes().size());
                    break;
                case EDGES:
                    assertEquals(message, expected, graph.edges().size());
                    break;
            }
            return this;
        }

        public Steps thenNetworkHas(int expected, Types types) {
            switch (types) {
                case NODES:
                    assertEquals("Unexpected number of nodes in graph.", expected, cyNetwork.getNodeList().size());
                    break;
                case EDGES:
                    assertEquals("Unexpected number of edges in graph.", expected, cyNetwork.getEdgeList().size());
                    break;
            }
            return this;
        }


        private String randomLabel() {
            return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
        }

        private CypherQuery importAllNodesAndEdges(String networkLabel) {
            String query = MessageFormat.format(TaskConstants.MATCH_ALL_NODES_AND_EDGES, networkLabel);
            return CypherQuery.builder().query(query).build();
        }
    }

}
