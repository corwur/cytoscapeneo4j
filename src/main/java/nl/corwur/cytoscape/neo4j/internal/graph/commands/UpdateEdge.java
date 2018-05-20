package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;

import java.util.Map;

public class UpdateEdge extends GraphCommand {

    private PropertyKey<Long> startId;
    private PropertyKey<Long> endId;
    private Map<String, Object> properties;

    public UpdateEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        this.startId = startId;
        this.endId = endId;
        this.properties = properties;
    }

    public static UpdateEdge create(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        return new UpdateEdge(startId, endId, properties);
    }

    @Override
    public void execute() throws CommandException {
        try {
            graphImplementation.updateEdge(startId, endId, properties);
        } catch (GraphImplementationException e) {
            throw new CommandException(e);
        }
    }
}
