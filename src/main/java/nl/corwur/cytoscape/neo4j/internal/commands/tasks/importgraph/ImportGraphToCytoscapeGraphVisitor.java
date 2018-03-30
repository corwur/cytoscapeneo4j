package nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.commands.tasks.CancelTaskException;
import nl.corwur.cytoscape.neo4j.internal.graph.*;
import org.cytoscape.model.CyNetwork;

import java.util.List;
import java.util.function.Supplier;

/**
 * This class implements a graph visitor that uses an import strategy to copy nodes and edges to Cytoscape.
 */
public class ImportGraphToCytoscapeGraphVisitor implements GraphVisitor {

    private final CyNetwork network;
    private final ImportGraphStrategy importGraphStrategy;
    private final Supplier<Boolean> isCancelled;


    public ImportGraphToCytoscapeGraphVisitor(CyNetwork network, ImportGraphStrategy importGraphStrategy, Supplier<Boolean> isCancelled) {
        this.network = network;
        this.importGraphStrategy = importGraphStrategy;
        this.isCancelled = isCancelled;
    }

    public void importGraph(List<GraphObject> list) {
        cancel();
        importGraphStrategy.createTables(network);
        list.forEach(item -> item.accept(this));
        importGraphStrategy.postProcess(network);
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
    public void visit(GraphObjectList graphObjectList) {
        for (GraphObject graphObject : graphObjectList.getList()) {
            graphObject.accept(this);
        }
    }

    @Override
    public void visit(GraphResult result) {
        cancel();
        result.getAll().forEach(graphObject -> graphObject.accept(this));
    }

    @Override
    public void visit(GraphLong graphLong) {
        /* No action defined for long */
    }

    @Override
    public void visit(GraphUnspecifiedType unspecifiedType) {
        /* No action defined for unspecifiedType */
    }

    private void cancel() {
        if(isCancelled.get()) {
            throw new CancelTaskException("Import canceled");
        }
    }
}
