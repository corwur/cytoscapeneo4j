package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping;

public interface MappingStrategy {
    void accept(MappingStrategyVisitor visitor);
}
