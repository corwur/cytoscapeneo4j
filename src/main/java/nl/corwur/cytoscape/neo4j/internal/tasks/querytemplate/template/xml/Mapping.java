package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.xml;

public interface Mapping {
    void accept(MappingVisitor visitor);
}
