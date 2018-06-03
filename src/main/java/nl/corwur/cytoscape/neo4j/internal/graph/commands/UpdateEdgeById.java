package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.PropertyKey;

import java.util.Map;

/**
 * Update an edge
 */
public class UpdateEdgeById extends GraphCommand {

    private PropertyKey<Long> edgeId;
    private Map<String, Object> properties;

    public UpdateEdgeById(PropertyKey<Long> edgeId, Map<String, Object> properties) {
        this.edgeId = edgeId;
        this.properties = properties;
    }

    public static UpdateEdgeById create(PropertyKey<Long> edgeId, Map<String, Object> properties) {
        return new UpdateEdgeById(edgeId, properties);
    }

    @Override
    public void execute() throws CommandException {
        try {
            graphImplementation.updateEdgeById(edgeId, properties);
        } catch (GraphImplementationException e) {
            throw new CommandException(e);
        }
    }
}
