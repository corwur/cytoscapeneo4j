package nl.corwur.cytoscape.neo4j.internal.tasks;

public final class TaskConstants {

    public static final String NEO4J_PROPERTY_CYTOSCAPE_NETWORK = "_cytoscape_network";
    public static final String MATCH_ALL_NODES_AND_EDGES = "match (n '{'" + NEO4J_PROPERTY_CYTOSCAPE_NETWORK + ":''{0}'''}')-[r*0..1]-(m '{'_cytoscape_network:''{0}'''}') RETURN n,r,m";



    private TaskConstants () {

    }
}
