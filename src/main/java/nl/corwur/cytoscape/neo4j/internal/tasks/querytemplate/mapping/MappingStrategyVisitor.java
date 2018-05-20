package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping;

public interface MappingStrategyVisitor {
    void visit(GraphMapping graphMapping);
    void visit(CopyAllMappingStrategy copyAllMappingStrategy);
}
