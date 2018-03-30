package nl.corwur.cytoscape.neo4j.internal.commands.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;

import java.text.MessageFormat;

/**
 * This class imports all nodes and edges from neo4j into cytoscape.
 */
public class ImportAllNodesAndEdgesFromNeo4JTask extends AbstractImportTask {

    public static final String MATCH_ALL_NODES_AND_EDGES = "match (n:{0})-[r*0..]-(m:{0}) RETURN n,r,m";

    public ImportAllNodesAndEdgesFromNeo4JTask(Services services, String networkName, String visualStyleTitle) {
        super(services, networkName, visualStyleTitle, new DefaultImportStrategy(), CypherQuery.builder().query(formatQuery(networkName)).build());
    }

    private static String formatQuery(String network) {
        return MessageFormat.format(MATCH_ALL_NODES_AND_EDGES, network);
    }
}
