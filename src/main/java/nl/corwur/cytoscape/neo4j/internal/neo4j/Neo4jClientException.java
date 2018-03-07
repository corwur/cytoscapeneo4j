package nl.corwur.cytoscape.neo4j.internal.neo4j;

public class Neo4jClientException extends Exception {
    public Neo4jClientException(String message, Throwable t) {
        super(message, t);
    }

    public Neo4jClientException() {
        super();
    }
}
