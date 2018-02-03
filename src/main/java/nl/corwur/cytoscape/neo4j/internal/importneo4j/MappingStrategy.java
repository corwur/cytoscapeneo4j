package nl.corwur.cytoscape.neo4j.internal.importneo4j;

public interface MappingStrategy {
    void accept(MappingStrategyVisitor visitor);
}
