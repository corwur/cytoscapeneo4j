package nl.corwur.cytoscape.neo4j.internal.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.tasks.CancelTaskException;
import org.cytoscape.model.CyNetwork;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * This class implements a graph visitor that uses an import strategy to copy nodes and edges to Cytoscape.
 */
public class ImportGraphToCytoscape {

    private final CyNetwork network;
    private final ImportGraphStrategy importGraphStrategy;
    private final Supplier<Boolean> isCancelled;


    public ImportGraphToCytoscape(CyNetwork network, ImportGraphStrategy importGraphStrategy, Supplier<Boolean> isCancelled) {
        this.network = network;
        this.importGraphStrategy = importGraphStrategy;
        this.isCancelled = isCancelled;
    }

    /**
     * Import a graph asynchronous, check if the import is cancelled.
     *
     * @param graph
     */
    public void importGraph(Graph graph) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            importGraphStrategy.createTables(network);
            importGraphStrategy.copyGraph(network, graph);
            importGraphStrategy.postProcess(network);
        });
        while (!future.isDone()) {
            cancel();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void cancel() {
        if (isCancelled.get()) {
            throw new CancelTaskException("Import canceled");
        }
    }
}
