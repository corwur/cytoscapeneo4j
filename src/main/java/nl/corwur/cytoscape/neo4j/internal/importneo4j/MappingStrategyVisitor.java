package nl.corwur.cytoscape.neo4j.internal.importneo4j;

import nl.corwur.cytoscape.neo4j.internal.importneo4j.mapping.GraphMapping;

public interface MappingStrategyVisitor {
    void visit(GraphMapping graphMapping);
    void visit(CopyAllMappingStrategy copyAllMappingStrategy);
}
