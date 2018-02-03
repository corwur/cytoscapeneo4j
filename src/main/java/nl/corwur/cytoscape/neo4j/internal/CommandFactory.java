package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.importneo4j.CypherQueryTemplate;
import nl.corwur.cytoscape.neo4j.internal.graph.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.task.exportnetwork.ExportNetworkToNeo4jTask;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.CopyAllImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.ImportGraphTask;

public class CommandFactory {

    private final Services services;

    static CommandFactory create(Services services) {
        return new CommandFactory(services);
    }

    private CommandFactory(Services services) {
        this.services = services;
    }

    public ImportGraphTask createImportGraphTask() {
        CypherQuery cypherQuery = CypherQuery.builder().query("MATCH (n)-[r]->(m) RETURN n,r,m").build();
        return new ImportGraphTask(
                services,
                "Network",
                services.getVisualMappingManager().getDefaultVisualStyle().getTitle(),
                new CopyAllImportStrategy(),
                cypherQuery);
    }
    public ExportNetworkToNeo4jTask createExportNetworkToNeo4jTask(ExportNetworkConfiguration exportNetworkConfiguration) {
        return new ExportNetworkToNeo4jTask(services, exportNetworkConfiguration);
    }

    public ImportGraphTask createRetrieveDataFromQueryTemplateTask(String networkName, CypherQueryTemplate query, String visualStyle) {
        ImportGraphStrategySelector selector = new ImportGraphStrategySelector();
        query.getMapping().accept(selector);
        return new ImportGraphTask(services, networkName, visualStyle, selector.getImportGraphStrategy() ,query.createQuery());
    }
    public ImportGraphTask createExecuteCypherQueryTask(String networkName, CypherQuery query, String visualStyle) {
        return new ImportGraphTask(services, networkName, visualStyle, new CopyAllImportStrategy(), query);
    }
}
