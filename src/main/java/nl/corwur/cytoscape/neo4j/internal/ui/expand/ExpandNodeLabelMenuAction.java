package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.internal.value.ListValue;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;

public class ExpandNodeLabelMenuAction implements CyNodeViewContextMenuFactory {

	private DefaultImportStrategy importGraphStrategy;
	private Services services;
	private CyNetworkView networkView;
	private View<CyNode> nodeView;
	private JMenu menu;

	public ExpandNodeLabelMenuAction(Services services) {
        super();
        this.importGraphStrategy = new DefaultImportStrategy();
        this.services = services;
    }
	
	public void addMenuItemsNodes(Record record) {
		ListValue result = (ListValue) record.get("r");
        ArrayList<String> nodeLabels = new ArrayList<String>();
        result.asList().forEach(v -> nodeLabels.add((String)v));
        String nodeLabel = String.join(":", nodeLabels);
		JMenuItem menuItem = new JMenuItem(nodeLabel);
		menuItem.addActionListener(new ExpandNodeTask(nodeView, networkView, this.services, true, null, nodeLabel));			
		this.menu.add(menuItem);

    }


	@Override
	public CyMenuItem createMenuItem(CyNetworkView networkView, View<CyNode> nodeView) {
        this.networkView = networkView;
        this.nodeView = nodeView;
        CyNode cyNode = (CyNode) nodeView.getModel();
		try {
	        Long refid = networkView.getModel().getRow(cyNode).get(this.importGraphStrategy.getRefIDName(), Long.class);

	        String query = "match (n)-[]-(r) where ID(n) = " + refid + " return distinct labels(r) as r";
	        CypherQuery cypherQuery = CypherQuery.builder().query(query).build();
	        StatementResult result = this.services.getNeo4jClient().getResults(cypherQuery);
	        this.menu = new JMenu("Expand node to:");
	        result.forEachRemaining(this::addMenuItemsNodes);
	        CyMenuItem cyMenuItem = new CyMenuItem(this.menu, 0.5f);

	        
	        return cyMenuItem;

		} catch (Neo4jClientException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}

		return null;
	}

}
