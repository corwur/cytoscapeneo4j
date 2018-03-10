package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.template.xml;

public interface Mapping {
    void accept(MappingVisitor visitor);
}
