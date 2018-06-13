package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphToCytoscape;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ExpandNodeTask extends AbstractNodeViewTask implements Task, ActionListener {

	public enum Direction {
		IN,
		OUT,
		BIDIRECTIONAL;
	}
    private final transient Services services;
    private final ImportGraphStrategy importGraphStrategy;
    private Boolean redoLayout;
    private String edge;
    private String node;
	private Direction direction; 
    
	public ExpandNodeTask(View<CyNode> nodeView, CyNetworkView networkView, Services services, Boolean redoLayout) {
		super(nodeView, networkView);
		this.services = services;
		this.importGraphStrategy = new DefaultImportStrategy();
		this.redoLayout = redoLayout;
		this.edge = null;
		this.node = null;
		this.direction = ExpandNodeTask.Direction.BIDIRECTIONAL;
	}
	
	public void setNode(String node) {
		this.node = node;
	}

	public void setEdge(String edge) {
		this.edge = edge;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	
	private void expand() throws InterruptedException, ExecutionException {
		CyNode cyNode = (CyNode)this.nodeView.getModel();
		
		Long refid = this.netView.getModel().getRow(cyNode).get(this.importGraphStrategy.getRefIDName(), Long.class);
		String directionLeft = this.direction == Direction.IN ? "<" : "";
		String directionRight = this.direction == Direction.OUT ? ">" : "";
		
		String query;
		if (this.edge == null && this.node == null) {
			query = "match p=(n)" + directionLeft +  "-[r]-" + directionRight + "() where ID(n) = " + refid +" return p"; 
		}
		else if (this.node == null){
			query = "match p=(n)" + directionLeft +  "-[:"+this.edge+"]-" + directionRight + "() where ID(n) = " + refid +" return p";
		}
		else {
			query = "match p=(n)" + directionLeft +  "-[r]-" + directionRight + "(:" + this.node + ") where ID(n) = " + refid +" return p"; 
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
            throw new IllegalStateException("Error executing cypher query");
        }

        Graph graph = result.get();

        ImportGraphToCytoscape cypherParser = new ImportGraphToCytoscape(this.netView.getModel(), importGraphStrategy, () -> this.cancelled);

        cypherParser.importGraph(graph);
        if (this.redoLayout) {
            CyLayoutAlgorithm layout = services.getCyLayoutAlgorithmManager().getDefaultLayout();
            Set<View<CyNode>> nodes = new HashSet<>();
            insertTasksAfterCurrentTask(layout.createTaskIterator(this.netView, layout.createLayoutContext(), nodes, null));
        }
        services.getVisualMappingManager().getVisualStyle(this.netView).apply(this.netView);
        this.netView.updateView();

    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {

        taskMonitor.setTitle("Expanding node");
        this.expand();

    }

    private Graph getGraph(CypherQuery query) {
        try {
            return services.getNeo4jClient().getGraph(query);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            this.expand();
        } catch (Exception exception) {

        }
    }

}
