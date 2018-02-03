package nl.corwur.cytoscape.neo4j.internal.importneo4j.xml;

public interface Mapping {
    void accept(MappingVisitor visitor);
}
