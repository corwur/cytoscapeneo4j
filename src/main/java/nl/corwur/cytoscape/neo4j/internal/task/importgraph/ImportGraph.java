package nl.corwur.cytoscape.neo4j.internal.task.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.*;
import nl.corwur.cytoscape.neo4j.internal.task.CancelTaskException;
import org.cytoscape.model.CyNetwork;

import java.util.List;
import java.util.function.Supplier;

public class ImportGraph implements GraphVisitor {

    private final CyNetwork network;
    private final ImportGraphStrategy importGraphStrategy;
    private final Supplier<Boolean> isCancelled;


    public ImportGraph(CyNetwork network, ImportGraphStrategy importGraphStrategy, Supplier<Boolean> isCancelled) {
        this.network = network;
        this.importGraphStrategy = importGraphStrategy;
        this.isCancelled = isCancelled;
    }


    public void importGraph(List<GraphObject> list) {
        cancel();
        importGraphStrategy.createTables(network);
        list.forEach(item -> item.accept(this));
    }

    @Override
    public void visit(GraphNode graphNode) {
        cancel();
        importGraphStrategy.handleNode(network, graphNode);
    }

    @Override
    public void visit(GraphEdge graphEdge) {
        cancel();
        importGraphStrategy.handleEdge(network, graphEdge);
    }

    @Override
    public void visit(GraphPath graphPath) {
        cancel();
        for(GraphNode node : graphPath.getNodes()) {
            node.accept(this);
        }
        for(GraphEdge edge : graphPath.getEdges()) {
            edge.accept(this);
        }
    }

    @Override
    public void visit(GraphResult result) {
        cancel();
        result.getAll().forEach(graphObject -> graphObject.accept(this));
    }

    @Override
    public void visit(GraphLong graphLong) {
    }

    @Override
    public void visit(GraphUnspecifiedType unspecifiedType) {
    }

    private void cancel() {
        if(isCancelled.get()) {
            throw new CancelTaskException("Import canceled");
        }
    }
}
