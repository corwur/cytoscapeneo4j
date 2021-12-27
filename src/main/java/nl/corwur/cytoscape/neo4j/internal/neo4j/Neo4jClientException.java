package nl.corwur.cytoscape.neo4j.internal.neo4j;

public class Neo4jClientException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7726489321687970100L;

	public Neo4jClientException(String message, Throwable t) {
        super(message, t);
    }

    public Neo4jClientException() {
        super();
    }

	public Neo4jClientException(String message) {
		super(message);
	}
}
