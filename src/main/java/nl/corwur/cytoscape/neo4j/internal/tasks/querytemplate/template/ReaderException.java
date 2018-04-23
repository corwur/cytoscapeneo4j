package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.template;

public class ReaderException extends Exception {
    public ReaderException(String msg) {
        super(msg);
    }

    public ReaderException(String msg, Throwable e) {
        super(msg, e);
    }
}
