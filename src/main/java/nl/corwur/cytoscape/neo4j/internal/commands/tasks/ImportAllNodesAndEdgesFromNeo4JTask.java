package nl.corwur.cytoscape.neo4j.internal.commands.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;

/**
 * This class imports all nodes and edges from neo4j into cytoscape.
 */
public class ImportAllNodesAndEdgesFromNeo4JTask extends AbstractImportTask {

    public ImportAllNodesAndEdgesFromNeo4JTask(Services services, String networkName, String visualStyleTitle) {
        super(services, networkName, visualStyleTitle, new DefaultImportStrategy(), CypherQuery.builder().query("match (n)-[r*0..]-(m) RETURN n,r,m").build());
    }
}
