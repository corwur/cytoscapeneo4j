package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.Command;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.CommandException;

import java.util.ArrayList;
import java.util.List;

public class ComposedCommand extends Command {

    private final List<Command> commands = new ArrayList<>();

    private ComposedCommand(List<Command> commandList) {
        commands.addAll(commandList);
    }

    @Override
    public void execute() throws CommandException {
        for(Command command : commands) {
            command.execute();
        }
    }

    public void add(Command command) {
        commands.add(command);
    }

    public static Command create(List<Command> commandList) {
        return new ComposedCommand(commandList);
    }
}
