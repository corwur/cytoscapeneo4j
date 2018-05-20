package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;

public class RemoveNode  extends GraphCommand  {

    private final PropertyKey<Long> nodeId;

    private RemoveNode(PropertyKey<Long> nodeId) {
        this.nodeId = nodeId;
    }

    public static RemoveNode create(PropertyKey<Long> nodeId) {
        return new RemoveNode(nodeId);
    }

    @Override
    public void execute() throws CommandException {
        try {
            graphImplementation.removeNode(nodeId);
        } catch (GraphImplementationException e) {
            throw new CommandException(e);
        }
    }
}
