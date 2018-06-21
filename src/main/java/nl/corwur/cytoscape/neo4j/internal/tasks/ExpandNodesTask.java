package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask.Direction;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskMonitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ExpandNodesTask extends AbstractNetworkTask {
    private final transient Services services;
    private final ImportGraphStrategy importGraphStrategy;
    private Boolean onlySelected;
    private Direction direction;

    public ExpandNodesTask(Services services, CyNetwork network, Boolean onlySelected, Direction direction) {
        super(network);
        this.services = services;
        this.importGraphStrategy = new DefaultImportStrategy();
        this.onlySelected = onlySelected;
        this.direction = direction;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Connecting nodes");

        List<CyRow> rows = this.network.getDefaultNodeTable().getAllRows();
        ArrayList<String> ids = new ArrayList<String>();
        for (CyRow row : rows) {
            if (this.onlySelected && row.get(CyNetwork.SELECTED, Boolean.class)) {
                ids.add(row.get(this.importGraphStrategy.getRefIDName(), Long.class).toString());
            } else if (!this.onlySelected) {
                ids.add(row.get(this.importGraphStrategy.getRefIDName(), Long.class).toString());
            }
        }
        String idsQuery = "[" + String.join(",", ids) + "]";
        String query = "";
        switch (this.direction) {
            case BIDIRECTIONAL:
                query = "match p=(n)-[r]-(m) where ID(n) in " + idsQuery + " return p";
                break;
            case IN:
                query = "match p=(n)<-[r]-(m) where ID(n) in " + idsQuery + " return p";
                break;
            case OUT:
                query = "match p=(n)-[r]->(m) where ID(n) in " + idsQuery + " return p";
                break;
        }

        CypherQuery cypherQuery = CypherQuery.builder().query(query).build();

        CompletableFuture<Graph> result = CompletableFuture.supplyAsync(() -> getGraph(cypherQuery));

        while (!result.isDone()) {
            if (this.cancelled) {
                result.cancel(true);
            }
            Thread.sleep(400);
        }
        if (result.isCompletedExceptionally()) {
            taskMonitor.setStatusMessage("Could not connect nodes. Are you still connected to the database?");
            throw new IllegalStateException("Error executing cypher query");
        }

        Graph graph = result.get();

        taskMonitor.setStatusMessage("Importing the Neo4j Graph");
        ImportGraphToCytoscape cypherParser = new ImportGraphToCytoscape(this.network, importGraphStrategy, () -> this.cancelled);

        cypherParser.importGraph(graph);

        for (CyNetworkView cyNetworkView : this.services.getCyNetworkViewManager().getNetworkViews(network)) {
            services.getVisualMappingManager().getVisualStyle(cyNetworkView).apply(cyNetworkView);
            CyLayoutAlgorithm layout = services.getCyLayoutAlgorithmManager().getDefaultLayout();
            Set<View<CyNode>> nodes = new HashSet<>();
            insertTasksAfterCurrentTask(layout.createTaskIterator(cyNetworkView, layout.createLayoutContext(), nodes, null));
            cyNetworkView.updateView();
        }
    }

    private Graph getGraph(CypherQuery query) {
        try {
            return services.getNeo4jClient().getGraph(query);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
