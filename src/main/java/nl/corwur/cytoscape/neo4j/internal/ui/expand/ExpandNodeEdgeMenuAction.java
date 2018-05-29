package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask.Direction;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import java.util.ArrayList;

import javax.swing.*;


public class ExpandNodeEdgeMenuAction implements CyNodeViewContextMenuFactory {

    private final transient Services services;
    private ImportGraphStrategy importGraphStrategy;
    private JMenu menu;
    private CyNetworkView networkView;
    private View<CyNode> nodeView;
    private Direction direction;
    
    public ExpandNodeEdgeMenuAction(Services services) {
        super();
        this.importGraphStrategy = new DefaultImportStrategy();
        this.services = services;
    }

	public void addMenuItemsEdges(Record record) {
		String result = record.get("r","");
		String menuTitle = this.direction == Direction.IN ? "<-" : "-";
		menuTitle = menuTitle + result + (this.direction == Direction.OUT ? "->" : "-");
		JMenuItem menuItem = new JMenuItem(menuTitle);
		ExpandNodeTask expandNodeTask = new ExpandNodeTask(nodeView, networkView, this.services, true);
		expandNodeTask.setEdge(result);
		menuItem.addActionListener(expandNodeTask);
		
		this.menu.add(menuItem);
    }
		
	
	
    @Override
    public CyMenuItem createMenuItem(CyNetworkView networkView, View<CyNode> nodeView) {
        this.networkView = networkView;
        this.nodeView = nodeView;
        CyNode cyNode = (CyNode) nodeView.getModel();
		try {
	        Long refid = networkView.getModel().getRow(cyNode).get(this.importGraphStrategy.getRefIDName(), Long.class);
	        this.menu = new JMenu("Expand node on:");

	        this.direction = Direction.BIDIRECTIONAL;
	        String query = "match (n)-[r]-() where ID(n) = " + refid + " return distinct type(r) as r";
	        CypherQuery cypherQuery = CypherQuery.builder().query(query).build();
	        StatementResult result = this.services.getNeo4jClient().getResults(cypherQuery);
	        result.forEachRemaining(this::addMenuItemsEdges);
	        
	        this.direction = Direction.IN;
	        query = "match (n)<-[r]-() where ID(n) = " + refid + " return distinct type(r) as r";
	        cypherQuery = CypherQuery.builder().query(query).build();
	        result = this.services.getNeo4jClient().getResults(cypherQuery);
	        result.forEachRemaining(this::addMenuItemsEdges);
	        
	        this.direction = Direction.OUT;
	        query = "match (n)-[r]->() where ID(n) = " + refid + " return distinct type(r) as r";
	        cypherQuery = CypherQuery.builder().query(query).build();
	        result = this.services.getNeo4jClient().getResults(cypherQuery);
	        result.forEachRemaining(this::addMenuItemsEdges);
	        
	        CyMenuItem cyMenuItem = new CyMenuItem(this.menu, 0.5f);
	        
	        return cyMenuItem;

		} catch (Neo4jClientException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return null;

    }


}
