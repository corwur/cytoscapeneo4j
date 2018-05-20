package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class CommandBuilder {

    private GraphImplementation graphImplementation;
    private List<Command> commandList = new ArrayList<>();

    public CommandBuilder graphImplementation(GraphImplementation graphImplementation) {
        this.graphImplementation = graphImplementation;
        return this;
    }

    public CommandBuilder addEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        AddEdge addEdge = AddEdge.create(startId, endId, properties);
        addEdge.setGraphImplementation(graphImplementation);
        commandList.add(addEdge);
        return this;
    }


    public CommandBuilder  updateNode(PropertyKey<Long> nodeId, List<NodeLabel> labels, Map<String, Object> properties) {
        UpdateNode updateNode = UpdateNode.create(nodeId, labels, properties);
        updateNode.setGraphImplementation(graphImplementation);
        commandList.add(updateNode);
        return this;
    }

    public CommandBuilder addNode(List<NodeLabel> labels, Map<String, Object> properties) {
        AddNode addNode = AddNode.create(labels, properties);
        addNode.setGraphImplementation(graphImplementation);
        commandList.add(addNode);
        return this;
    }

    public CommandBuilder removeNode(PropertyKey<Long> nodeId) {
        RemoveNode removeNode = RemoveNode.create(nodeId);
        removeNode.setGraphImplementation(graphImplementation);
        commandList.add(removeNode);
        return this;
    }

    public CommandBuilder updateEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) {
        UpdateEdge updateEdge = UpdateEdge.create(startId, endId, properties);
        updateEdge.setGraphImplementation(graphImplementation);
        commandList.add(updateEdge);
        return this;
    }

    public CommandBuilder removeEdge(PropertyKey<Long> edgeId) {
        RemoveEdge removeEdge = RemoveEdge.create(edgeId);
        removeEdge.setGraphImplementation(graphImplementation);
        commandList.add(removeEdge);
        return this;
    }

    public CommandBuilder sort(Comparator<Command> comparator) {
        commandList.sort(comparator);
        return this;
    }

    public Command build() {
        return ComposedCommand.create(commandList);
    }
}
