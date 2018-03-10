package nl.corwur.cytoscape.neo4j.internal.commands;

import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.CopyAllMappingStrategy;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.MappingStrategyVisitor;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.GraphMapping;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.GraphMappingImportStrategy;

public class ImportGraphStrategySelector  {
    public ImportGraphStrategy getImportGraphStrategy(GraphMapping graphMapping) {
        return new GraphMappingImportStrategy(graphMapping);
    }
    public ImportGraphStrategy getImportGraphStrategy(CopyAllMappingStrategy copyAllMappingStrategy) {
        return new DefaultImportStrategy();
    }

}
