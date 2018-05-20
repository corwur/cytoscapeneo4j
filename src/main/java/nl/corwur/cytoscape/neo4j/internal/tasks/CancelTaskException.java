package nl.corwur.cytoscape.neo4j.internal.tasks;

public class CancelTaskException extends RuntimeException {
    public CancelTaskException(String msg) {
        super(msg);
    }
}
