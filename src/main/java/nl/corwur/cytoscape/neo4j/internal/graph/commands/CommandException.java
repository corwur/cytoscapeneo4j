package nl.corwur.cytoscape.neo4j.internal.graph.commands;

public class CommandException extends Exception {
    public CommandException(Exception e) {
        super(e);
    }
}
