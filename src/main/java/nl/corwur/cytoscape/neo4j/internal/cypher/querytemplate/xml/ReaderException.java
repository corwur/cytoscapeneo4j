package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.xml;

public class ReaderException extends Exception {
    public ReaderException(String msg) {
        super(msg);
    }

    public ReaderException(String msg, Throwable e) {
        super(msg, e);
    }
}
