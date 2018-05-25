package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.CypherQueryTemplate;

/**
 * This class creates executable commands:
 * - Import all nodes and edges
 * - Export network to Neo4j
 * - Import query template
 * - Import a query
 */
public class TaskFactory {

    private final Services services;

    public static TaskFactory create(Services services) {
        return new TaskFactory(services);
    }

    private TaskFactory(Services services) {
        this.services = services;
    }

    public ImportAllNodesAndEdgesFromNeo4JTask createImportAllNodesAndEdgesFromNeo4jTask(String network, String visualStyle) {
        return new ImportAllNodesAndEdgesFromNeo4JTask(
                services,
                network,
                visualStyle);
    }

    public ExportNetworkToNeo4jTask createExportNetworkToNeo4jTask(ExportNetworkConfiguration exportNetworkConfiguration) {
        return new ExportNetworkToNeo4jTask(services, exportNetworkConfiguration);
    }

    public ImportQueryTemplateTask createImportQueryTemplateTask(String networkName, CypherQueryTemplate queryTemplate, String visualStyle) {
        return new ImportQueryTemplateTask(services, networkName, visualStyle, queryTemplate);
    }

    public AbstractImportTask createImportQueryTask(String networkName, CypherQuery query, String visualStyle) {
        return new ImportQueryTask(services, networkName, visualStyle, new DefaultImportStrategy(), query);
    }
}
