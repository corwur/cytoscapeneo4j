package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyRow;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ShortestPathTask extends AbstractNetworkTask implements Task {

    private Services services;
    private DefaultImportStrategy importGraphStrategy;

    public ShortestPathTask(Services services, CyNetwork network) {
        super(network);
        this.services = services;
        this.importGraphStrategy = new DefaultImportStrategy();
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        taskMonitor.setTitle("Finding shortest path");

        // get all ids
        List<CyRow> rows = this.network.getDefaultNodeTable().getAllRows();
        ArrayList<String> ids = new ArrayList<String>();
        for (CyRow row : rows) {
            if (row.get(CyNetwork.SELECTED, Boolean.class)) {
                ids.add(row.get(this.importGraphStrategy.getRefIDName(), Long.class).toString());
            }
        }

        if (ids.size() <= 1) {
            return;
        }

        HashMap<String, String> queryPaths = new HashMap<String, String>();
        int count = 0;

        for (int i = 0; i < ids.size(); i++) {
            for (int j = i + 1; j < ids.size(); j++) {
                String path = "p_" + i + "_" + j;
                String in = String.format("[%s, %s]", ids.get(i), ids.get(j));
                String match = String.format("match %s=shortestpath((n%d)-[*]-(m%d)) where ID(n%d) in %s and ID(m%d) in %s and ID(n%d) <> ID(m%d)",
                        path, count, count, count, in, count, in, count, count);
                queryPaths.put(path, match);
                count++;
            }
        }
        String query = String.join("\n", queryPaths.values()) + "\n";
        query += "return " + String.join(",", queryPaths.keySet());

        CypherQuery cypherQuery = CypherQuery.builder().query(query).build();

        CompletableFuture<Graph> result = CompletableFuture.supplyAsync(() -> getGraph(cypherQuery));

        while (!result.isDone()) {
            if (this.cancelled) {
                result.cancel(true);
            }
            Thread.sleep(400);
        }
        if (result.isCompletedExceptionally()) {
            taskMonitor.setStatusMessage("Could not find shortest path(s). Are you still connected to the database?");
            throw new IllegalStateException("Error executing cypher query");
        }

        Graph graph = result.get();

        taskMonitor.setStatusMessage("Importing the Neo4j Graph");
        ImportGraphToCytoscape cypherParser = new ImportGraphToCytoscape(this.network, importGraphStrategy, () -> this.cancelled);

        cypherParser.importGraph(graph);
        for (CyNetworkView cyNetworkView : this.services.getCyNetworkViewManager().getNetworkViews(network)) {
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
