package nl.corwur.cytoscape.neo4j.internal.graph.commands;

public abstract class Command {
    public abstract void execute() throws CommandException;
}
