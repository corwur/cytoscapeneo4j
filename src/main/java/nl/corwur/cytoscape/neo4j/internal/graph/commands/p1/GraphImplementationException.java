package nl.corwur.cytoscape.neo4j.internal.graph.commands.p1;

import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;

public class GraphImplementationException extends Exception {

    public GraphImplementationException(Exception e) {
        super(e);
    }
}
