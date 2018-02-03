package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.importneo4j.CopyAllMappingStrategy;
import nl.corwur.cytoscape.neo4j.internal.importneo4j.MappingStrategyVisitor;
import nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.GraphMapping;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.CopyAllImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.task.importgraph.MappingImportStrategy;

public class ImportGraphStrategySelector implements MappingStrategyVisitor {
    private ImportGraphStrategy importGraphStrategy;

    @Override
    public void visit(GraphMapping graphMapping) {
        importGraphStrategy = new MappingImportStrategy(graphMapping);
    }

    @Override
    public void visit(CopyAllMappingStrategy copyAllMappingStrategy) {
        importGraphStrategy = new CopyAllImportStrategy();
    }

    public ImportGraphStrategy getImportGraphStrategy() {
        return importGraphStrategy;
    }
}
