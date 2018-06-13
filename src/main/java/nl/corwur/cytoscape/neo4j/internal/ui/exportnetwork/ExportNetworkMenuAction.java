package nl.corwur.cytoscape.neo4j.internal.ui.exportnetwork;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.Label;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExportNetworkToNeo4jTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;

import static nl.corwur.cytoscape.neo4j.internal.tasks.TaskConstants.CYCOLUMN_NEO4J_LABELS;

public class ExportNetworkMenuAction extends AbstractCyAction {

    /**
     *
     */
    private static final long serialVersionUID = -3105483618300742403L;
    private static final String MENU_TITLE = "Export Network to Neo4j";
    private static final String MENU_LOC = "Apps.Cypher Queries";

    private final transient Services services;

    public static ExportNetworkMenuAction create(Services services) {
        return new ExportNetworkMenuAction(services);
    }

    private ExportNetworkMenuAction(Services services) {
        super(MENU_TITLE);
        this.services = services;
        setPreferredMenu(MENU_LOC);
        setEnabled(true);
        setMenuGravity(0.5f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (!DialogMethods.connect(services)) {
            return;
        }

        Label label = getNodeLabel();
        ExportNetworkConfiguration exportNetworkConfiguration = ExportNetworkConfiguration.create(
                label,
                "shared name",
                "refid",
                CYCOLUMN_NEO4J_LABELS,
                "_neo4j_properties"
        );
        if (label != null) {
            ExportNetworkToNeo4jTask task = services.getTaskFactory().createExportNetworkToNeo4jTask(exportNetworkConfiguration);
            services.getTaskExecutor().execute(task);
        }
    }

    private Label getNodeLabel() {
        String message = "Enter the name for this network";
        CyNetwork currentNetwork = services.getCyApplicationManager().getCurrentNetwork();
        String initialValue = currentNetwork.getRow(currentNetwork).get(CyNetwork.NAME, String.class);

        while (true) {
            String label = JOptionPane.showInputDialog(services.getCySwingApplication().getJFrame(), message, initialValue);
            if (label != null) {
                try {
                    return NodeLabel.create(label);
                } catch (Exception e) {
                    message = "Error in network name ([A-Za-z0-9])";
                }
            } else {
                return null;
            }
        }
    }
}
