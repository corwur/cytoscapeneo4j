package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.mapping.GraphMapping;

public interface MappingStrategyVisitor {
    void visit(GraphMapping graphMapping);
    void visit(CopyAllMappingStrategy copyAllMappingStrategy);
}
