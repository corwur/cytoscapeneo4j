package nl.corwur.cytoscape.neo4j.internal.graph;

public abstract class GraphObject {
   public abstract void accept(GraphVisitor graphVisitor);
}
