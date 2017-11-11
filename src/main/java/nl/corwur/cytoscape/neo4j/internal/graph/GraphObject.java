package nl.corwur.cytoscape.neo4j.internal.graph;

public interface GraphObject {
   void accept(GraphVisitor graphVisitor);
}
