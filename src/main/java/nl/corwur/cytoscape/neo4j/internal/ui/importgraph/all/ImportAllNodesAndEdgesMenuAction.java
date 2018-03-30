package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.all;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.AbstractImportTask;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.vizmap.VisualStyle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

public class ImportAllNodesAndEdgesMenuAction extends AbstractCyAction {

    private static final String MENU_TITLE = "Import all nodes and edges from Neo4j";
    private static final String MENU_LOC = "Apps.Cypher Queries";

    private final transient Services services;

    public static ImportAllNodesAndEdgesMenuAction create(Services services) {
        return new ImportAllNodesAndEdgesMenuAction(services);
    }

    private ImportAllNodesAndEdgesMenuAction(Services services) {
        super(MENU_TITLE);
        this.services = services;
        setPreferredMenu(MENU_LOC);
        setEnabled(false);
        setMenuGravity(0.5f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(!DialogMethods.connect(services)) {
            return;
        }

        ImportAllNodesAndEdgesDialog importAllNodesAndEdgesDialog = new ImportAllNodesAndEdgesDialog(services.getCySwingApplication().getJFrame(), getAllVisualStyleTitle());
        importAllNodesAndEdgesDialog.showDialog();

        AbstractImportTask importAllNodesAndEdgesFromNeo4jTask =
                services.getCommandFactory().createImportAllNodesAndEdgesFromNeo4jTask(
                        importAllNodesAndEdgesDialog.getNetwork(),
                        importAllNodesAndEdgesDialog.getVisualStyleTitle()
                );
            services.getCommandExecutor().execute(importAllNodesAndEdgesFromNeo4jTask);
    }

    private String[] getAllVisualStyleTitle() {
        return services.getVisualMappingManager()
                .getAllVisualStyles().stream()
                .map(VisualStyle::getTitle)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
}
