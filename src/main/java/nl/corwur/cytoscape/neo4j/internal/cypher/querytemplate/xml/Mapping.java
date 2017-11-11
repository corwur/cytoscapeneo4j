package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.xml;

public interface Mapping {
    void accept(MappingVisitor visitor);
}
