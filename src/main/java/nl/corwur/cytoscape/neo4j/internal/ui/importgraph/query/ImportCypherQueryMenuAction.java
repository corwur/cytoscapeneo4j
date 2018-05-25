package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.query;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import nl.corwur.cytoscape.neo4j.internal.tasks.AbstractImportTask;
import nl.corwur.cytoscape.neo4j.internal.ui.DialogMethods;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.view.vizmap.VisualStyle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.stream.Collectors;

public class ImportCypherQueryMenuAction extends AbstractCyAction {

    private static final String MENU_TITLE = "Import Cypher Query";
    private static final String MENU_LOC = "Apps.Cypher Queries";

    private final transient Services services;

    public static ImportCypherQueryMenuAction create(Services services) {
        return new ImportCypherQueryMenuAction(services);
    }

    private ImportCypherQueryMenuAction(Services services) {
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
        boolean finished = false;
        CypherQueryDialog cypherQueryDialog = new CypherQueryDialog(services.getCySwingApplication().getJFrame(), getAllVisualStyleTitle());
        do {
            cypherQueryDialog.showDialog();
            if (!cypherQueryDialog.isExecuteQuery()) {
                finished = true;
                break;
            }
            String query = cypherQueryDialog.getCypherQuery();
            if (query.isEmpty()) {
                JOptionPane.showMessageDialog(services.getCySwingApplication().getJFrame(), "Query is empty");
                break;
            }
            CypherQuery cypherQuery = CypherQuery.builder().query(query).build();
            try {
                services.getNeo4jClient().explainQuery(cypherQuery);
                AbstractImportTask executeCypherQueryTask =
                        services.getTaskFactory().createImportQueryTask(
                                cypherQueryDialog.getNetwork(),
                                cypherQuery,
                                cypherQueryDialog.getVisualStyleTitle()
                        );
                services.getTaskExecutor().execute(executeCypherQueryTask);
                finished = true;
            } catch (Neo4jClientException e1) {
                JOptionPane.showMessageDialog(services.getCySwingApplication().getJFrame(), e1.getMessage());
            }
            if (!finished) {
                cypherQueryDialog = new CypherQueryDialog(
                        services.getCySwingApplication().getJFrame(),
                        getAllVisualStyleTitle(),
                        cypherQueryDialog.getCypherQuery(),
                        cypherQueryDialog.getNetwork()
                );
            }
        } while (!finished);
    }

    private String[] getAllVisualStyleTitle() {
        return services.getVisualMappingManager()
                .getAllVisualStyles().stream()
                .map(VisualStyle::getTitle)
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }
}
