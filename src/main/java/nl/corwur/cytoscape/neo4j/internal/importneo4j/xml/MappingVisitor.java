package nl.corwur.cytoscape.neo4j.internal.importneo4j.xml;

public interface MappingVisitor {
    void visit(ColumnMapping columnMapping);
    void visit(CopyAll copyAll);
}
