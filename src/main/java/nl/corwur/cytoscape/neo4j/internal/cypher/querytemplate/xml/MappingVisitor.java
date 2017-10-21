package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.xml;

public interface MappingVisitor {
    void visit(ColumnMapping columnMapping);
    void visit(CopyAll copyAll);
}
