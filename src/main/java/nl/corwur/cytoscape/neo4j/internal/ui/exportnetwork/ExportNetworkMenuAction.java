package nl.corwur.cytoscape.neo4j.internal.ui.exportnetwork;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.task.exportnetwork.ExportNetworkToNeo4jTask;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectToNeo4j;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ExportNetworkMenuAction extends AbstractCyAction {

    private final static String MENU_TITLE = "Export Network to Neo4j";
    private final static String MENU_LOC = "Apps.Cypher Queries";

    private final Services services;

    public static ExportNetworkMenuAction create(Services services) {
        return new ExportNetworkMenuAction(services);
    }

    private ExportNetworkMenuAction(Services services) {
        super(MENU_TITLE);
        this.services = services;
        setPreferredMenu(MENU_LOC);
        setEnabled(false);
        setMenuGravity(0.5f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        ConnectToNeo4j connectToNeo4j = ConnectToNeo4j.create(services);
        if (!connectToNeo4j.connect()) {
            JOptionPane.showMessageDialog(services.getCySwingApplication().getJFrame(), "Not connected");
            ;
            return;
        }

        NodeLabel nodeLabel = getNodeLabel();
        if (nodeLabel != null) {
            ExportNetworkToNeo4jTask task = services.getCommandFactory().createExportNetworkToNeo4jTask(nodeLabel);
            services.getCommandRunner().execute(task);
        }
    }

    private NodeLabel getNodeLabel() {
        String message = "Enter node label for this network";
        CyNetwork currentNetwork = services.getCyApplicationManager().getCurrentNetwork();
        String initialValue = currentNetwork.getRow(currentNetwork).get(CyNetwork.NAME, String.class);

        while (true) {
            String label = JOptionPane.showInputDialog(services.getCySwingApplication().getJFrame(), message, initialValue);
            if (label != null) {
                try {
                    return NodeLabel.create(label);
                } catch (Exception e) {
                    message = "Error in node label ([A-Za-z0-9])";
                }
            } else {
                return null;
            }
        }
    }
}
