package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementationException;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;

import java.util.Map;

/**
 * This class implements the 'Add Edge' command that can be executed by a Neo4jClient.
 */
public class AddEdge extends GraphCommand {

    private String relationship = "relationsip";
    private Map<String, Object> properties;
    private PropertyKey<Long> startId;
    private PropertyKey<Long> endId;

    public static AddEdge create(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        return new AddEdge(startId, endId, properties);
    }

    private AddEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        this.startId = startId;
        this.endId = endId;
        this.properties = properties;
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
