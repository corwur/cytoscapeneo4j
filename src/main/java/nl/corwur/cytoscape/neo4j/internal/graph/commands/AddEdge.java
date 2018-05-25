package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.implementation.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.implementation.PropertyKey;

import java.util.Map;

/**
 * This class implements the 'Add Edge' command that can be executed by a Neo4jClient.
 */
public class AddEdge extends GraphCommand {

    private String relationship = "relationsip";
    private final Map<String, Object> properties;
    private final PropertyKey<Long> startId;
    private final PropertyKey<Long> endId;

    public static AddEdge create(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties, String relationship) {
        return new AddEdge(startId, endId, properties, relationship);
    }

    private AddEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties, String relationship) {
        this.startId = startId;
        this.endId = endId;
        this.properties = properties;
        this.relationship = relationship;
    }

    @Override
    public void execute() throws CommandException {
        try {
            graphImplementation.addEdge(startId, endId, relationship, properties);
        } catch (GraphImplementationException e) {
            throw new CommandException(e);
        }
    }
}
