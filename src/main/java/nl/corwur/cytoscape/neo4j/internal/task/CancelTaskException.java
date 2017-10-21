package nl.corwur.cytoscape.neo4j.internal.task;

public class CancelTaskException extends RuntimeException {
    public CancelTaskException(String msg) {
        super(msg);
    }
}
