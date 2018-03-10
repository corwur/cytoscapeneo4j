package nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml;

import nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml.ColumnMapping;
import nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml.CopyAll;

public interface MappingVisitor {
    void visit(ColumnMapping columnMapping);
    void visit(CopyAll copyAll);
}
