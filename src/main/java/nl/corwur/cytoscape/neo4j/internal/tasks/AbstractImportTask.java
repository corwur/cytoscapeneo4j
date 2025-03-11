package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQueryWriter;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This class imports the results of a cyher query into cytoscape.
 */
public abstract class AbstractImportTask extends AbstractTask {

    private final Services services;
    private final String networkName;
    private final String visualStyleTitle;
    private final ImportGraphStrategy importGraphStrategy;
    private final CypherQuery cypherQuery;

    public AbstractImportTask(Services services, String networkName, String visualStyleTitle, ImportGraphStrategy importGraphStrategy, CypherQuery cypherQuery) {
        this.services = services;
        this.networkName = networkName;
        this.visualStyleTitle = visualStyleTitle;
        this.importGraphStrategy = importGraphStrategy;
        this.cypherQuery = cypherQuery;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        try {

            taskMonitor.setStatusMessage("Execute query");
            //explainQuery(cypherQuery);
            taskMonitor.setStatusMessage(cypherQuery.getQuery());
            CompletableFuture<Graph> result = CompletableFuture.supplyAsync(() -> getGraph(cypherQuery));
            result.whenComplete((results, ex) -> {
                if (ex != null) {
                	taskMonitor.setStatusMessage("Exception occurred: " + ex.getCause());
                } else {
                	taskMonitor.setStatusMessage("Result: " + results);
                }
            });

            while (!result.isDone()) {
                if (this.cancelled) {
                    result.cancel(true);
                }
                Thread.sleep(400);
            }
            
            
            if (result.isCompletedExceptionally()) {
            	
                throw new IllegalStateException("Error executing cypher query");
            }

            Graph graph = result.get();

            taskMonitor.setTitle("Importing the Neo4j Graph " + networkName);

            // setup network
            CyNetwork network = services.getCyNetworkFactory().createNetwork();
            network.getRow(network).set(CyNetwork.NAME, networkName);
            setCypherQuery(network);

            services.getCyNetworkManager().addNetwork(network);

            ImportGraphToCytoscape cypherParser = new ImportGraphToCytoscape(network, importGraphStrategy, () -> this.cancelled);

            taskMonitor.setStatusMessage("Importing graph");
            cypherParser.importGraph(graph);

            CyEventHelper cyEventHelper = services.getCyEventHelper();
            cyEventHelper.flushPayloadEvents();

            taskMonitor.setStatusMessage("Creating View");
            CyNetworkView networkView = services.getCyNetworkViewFactory().createNetworkView(network);
            services.getCyNetworkViewManager().addNetworkView(networkView);

            taskMonitor.setStatusMessage("Applying Layout");
            Set<View<CyNode>> nodes = new HashSet<>();
            CyLayoutAlgorithm layout = services.getCyLayoutAlgorithmManager().getLayout("force-directed");
            insertTasksAfterCurrentTask(layout.createTaskIterator(networkView, layout.createLayoutContext(), nodes, null));

            VisualStyle visualStyle = services.getVisualMappingManager().getAllVisualStyles().stream()
                    .filter(vs -> vs.getTitle().equals(visualStyleTitle))
                    .findFirst().orElseGet(() -> services.getVisualMappingManager().getDefaultVisualStyle());
            visualStyle.apply(networkView);
            networkView.updateView();

        } catch (Exception e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, e.getMessage());
        }
    }

    private void setCypherQuery(CyNetwork network) throws IOException {
        StringWriter writer = new StringWriter();
        CypherQueryWriter cypherQueryWriter = new CypherQueryWriter(writer);
        cypherQueryWriter.write(cypherQuery);
        CyTable cyTable = network.getDefaultNetworkTable();
        if (cyTable.getColumn("cypher_query") == null) {
            network.getDefaultNetworkTable().createColumn("cypher_query", String.class, true);
        }
        network.getRow(network).set("cypher_query", writer.toString());
    }

    private void explainQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        services.getNeo4jClient().explainQuery(cypherQuery);
    }

    private Graph getGraph(CypherQuery query) {
        try {
            return services.getNeo4jClient().getGraph(query);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
