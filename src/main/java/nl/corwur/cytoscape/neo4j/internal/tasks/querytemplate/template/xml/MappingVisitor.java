package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.xml;

public interface MappingVisitor {
    void visit(ColumnMapping columnMapping);

    void visit(CopyAll copyAll);
}
