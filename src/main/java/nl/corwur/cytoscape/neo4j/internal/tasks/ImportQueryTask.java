package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;

public class ImportQueryTask extends AbstractImportTask {
    public ImportQueryTask(Services services, String networkName, String visualStyle, DefaultImportStrategy defaultImportStrategy, CypherQuery query) {
        super(services, networkName, visualStyle, defaultImportStrategy, query);
    }
}
