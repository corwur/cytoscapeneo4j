package nl.corwur.cytoscape.neo4j.internal.commands.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.CypherQueryTemplate;

/**
 * This class imports the results of a query template from neo4j into cytoscape.
 */
public class ImportQueryTemplateTask extends AbstractImportTask {

    public ImportQueryTemplateTask(Services services, String networkName, String visualStyleTitle, CypherQueryTemplate queryTemplate) {
        super(services, networkName, visualStyleTitle, queryTemplate.getImportGraphStrategy(), queryTemplate.createQuery());
    }
}
