package nl.corwur.cytoscape.neo4j.internal.ui.importgraph;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.ImportGraphTask;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.vizmap.VisualStyle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

public class CypherQueryMenuAction extends AbstractCyAction {

    private static final String MENU_TITLE = "Import Cypher Query";
    private static final String MENU_LOC = "Apps.Cypher Queries";

    private final transient Services services;

    public static CypherQueryMenuAction create(Services services) {
        return new CypherQueryMenuAction(services);
    }

    private CypherQueryMenuAction(Services services) {
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
        CypherQueryDialog cypherQueryDialog = new CypherQueryDialog(services.getCySwingApplication().getJFrame(), getAllVisualStyleTitle());
        cypherQueryDialog.showDialog();
        if(!cypherQueryDialog.isExecuteQuery()) {
            return;
        }
        String query = cypherQueryDialog.getCypherQuery();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(services.getCySwingApplication().getJFrame(), "Query is empty");
            return;
        }

        ImportGraphTask executeCypherQueryTask  =
                services.getCommandFactory().createExecuteCypherQueryTask(
                        cypherQueryDialog.getNetwork(),
                        CypherQuery.builder().query(query).build(),
                        cypherQueryDialog.getVisualStyleTitle()
                );
        services.getCommandExecutor().execute(executeCypherQueryTask);
    }

    private String[] getAllVisualStyleTitle() {
        return services.getVisualMappingManager()
                .getAllVisualStyles().stream()
                .map(VisualStyle::getTitle)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
}
