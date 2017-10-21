package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.xml;

public abstract class Mapping {
    abstract void accept(MappingVisitor visitor);
}
