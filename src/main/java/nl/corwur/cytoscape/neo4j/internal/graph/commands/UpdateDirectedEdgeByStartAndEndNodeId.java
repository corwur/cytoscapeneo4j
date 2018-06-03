package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.PropertyKey;

import java.util.Map;

/**
 * Update an edge
 */
public class UpdateDirectedEdgeByStartAndEndNodeId extends GraphCommand {

    private PropertyKey<Long> startId;
    private PropertyKey<Long> endId;
    private Map<String, Object> properties;

    public UpdateDirectedEdgeByStartAndEndNodeId(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        this.startId = startId;
        this.endId = endId;
        this.properties = properties;
    }

    public static UpdateDirectedEdgeByStartAndEndNodeId create(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        return new UpdateDirectedEdgeByStartAndEndNodeId(startId, endId, properties);
    }

    @Override
    public void execute() throws CommandException {
        try {
            graphImplementation.updateDirectedEdge(startId, endId, properties);
        } catch (GraphImplementationException e) {
            throw new CommandException(e);
        }
    }
}
