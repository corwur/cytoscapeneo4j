package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;
import org.cytoscape.application.swing.CyMenuItem;
import org.cytoscape.application.swing.CyNodeViewContextMenuFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;

import javax.swing.*;


public class ExpandNodeEdgeMenuAction implements CyNodeViewContextMenuFactory {

    private final transient Services services;
    private ImportGraphStrategy importGraphStrategy;
    private JMenu menu;
    private CyNetworkView networkView;
    private View<CyNode> nodeView;

    public ExpandNodeEdgeMenuAction(Services services) {
        super();
        this.importGraphStrategy = new DefaultImportStrategy();
        this.services = services;

    }

    public void addMenuItems(Record record) {
        JMenuItem menuItem = new JMenuItem(record.get("r", ""));
        menuItem.addActionListener(new ExpandNodeTask(nodeView, networkView, this.services, true, record.get("r", "")));
        menu.add(menuItem);

    }

    @Override
    public CyMenuItem createMenuItem(CyNetworkView networkView, View<CyNode> nodeView) {
        this.networkView = networkView;
        this.nodeView = nodeView;
        CyNode cyNode = (CyNode) nodeView.getModel();

        Long refid = networkView.getModel().getRow(cyNode).get(this.importGraphStrategy.getRefIDName(), Long.class);
        String query = "match (n)-[r]-() where ID(n) = " + refid + " return distinct type(r) as r";
        CypherQuery cypherQuery = CypherQuery.builder().query(query).build();
        StatementResult result = this.getEdges(cypherQuery);

        this.menu = new JMenu("Expand node on:");
        result.forEachRemaining(this::addMenuItems);
        CyMenuItem cyMenuItem = new CyMenuItem(this.menu, 0.5f);

        return cyMenuItem;
    }

    private StatementResult getEdges(CypherQuery query) {
        try {
            return services.getNeo4jClient().getResults(query);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
