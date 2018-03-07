package nl.corwur.cytoscape.neo4j.internal.task.importgraph;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObject;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ImportGraphTask extends AbstractTask {

    private final Services services;
    private final String networkName;
    private final String visualStyleTitle;
    private final ImportGraphStrategy importGraphStrategy;
    private final CypherQuery cypherQuery;

    public ImportGraphTask(Services services, String networkName, String visualStyleTitle, ImportGraphStrategy importGraphStrategy, CypherQuery cypherQuery) {
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
            explainQuery(cypherQuery);
            CompletableFuture<List<GraphObject>> result = CompletableFuture.supplyAsync(() -> executeQuery(cypherQuery));

            while(!result.isDone()) {
                if(this.cancelled) {
                    result.cancel(true);
                }
                Thread.sleep(400);
            }
            if(result.isCompletedExceptionally()) {
                throw new IllegalStateException("Error executing cypher query");
            }

            List<GraphObject> graphObjects = result.get();

            taskMonitor.setTitle("Importing the Neo4j Graph " + networkName);

            // setup network
            CyNetwork network = services.getCyNetworkFactory().createNetwork();
            network.getRow(network).set(CyNetwork.NAME, networkName);
            services.getCyNetworkManager().addNetwork(network);

            ImportGraph cypherParser = new ImportGraph(network, importGraphStrategy, () -> this.cancelled);

            taskMonitor.setStatusMessage("Importing graph");
            cypherParser.importGraph(graphObjects);

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

    private void explainQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        services.getNeo4jClient().explainQuery(cypherQuery);
    }

    private List<GraphObject> executeQuery(CypherQuery query) {
        try {
            return services.getNeo4jClient().executeQuery(query);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
