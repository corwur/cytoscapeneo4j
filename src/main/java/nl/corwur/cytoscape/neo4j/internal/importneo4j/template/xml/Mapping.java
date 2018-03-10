package nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml;

public interface Mapping {
    void accept(MappingVisitor visitor);
}
