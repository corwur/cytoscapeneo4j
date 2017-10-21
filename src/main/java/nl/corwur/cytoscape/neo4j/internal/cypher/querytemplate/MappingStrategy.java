package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate;

public interface MappingStrategy {
    void accept(MappingStrategyVisitor visitor);
}
