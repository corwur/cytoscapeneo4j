package nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.*;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;
import org.cytoscape.model.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExportDifference {

    private static final String REFID = "refid";
    private static final String SUID = "SUID";
    private final Graph graph;
    private final CyNetwork cyNetwork;
    private final CommandBuilder commandBuilder = new CommandBuilder();
    private final List<GraphNode> visitedNode = new ArrayList<>();
    private final List<GraphEdge> visitedEdges = new ArrayList<>();

    public static ExportDifference create(Graph graph, CyNetwork cyNetwork, GraphImplementation graphImplementation) {
        return new ExportDifference(graph, cyNetwork, graphImplementation);
    }

    private ExportDifference(Graph graph, CyNetwork cyNetwork, GraphImplementation graphImplementation) {
        this.graph = graph;
        this.cyNetwork = cyNetwork;
        this.commandBuilder.graphImplementation(graphImplementation);
    }

    public Command compute() {
        visitedNode.clear();
        for(CyNode cyNode : cyNetwork.getNodeList()) {
            if(nodeExistsInGraph(cyNode)) {
                visit(cyNode);
                commandBuilder.updateNode(refId(cyNode), labels(cyNode), properties(cyNode));
            } else {
                commandBuilder.addNode(labels(cyNode), properties(cyNode));
            }
        }

        unvisitedNodes().forEach( removeNode -> commandBuilder.removeNode(new PropertyKey<>("id",removeNode.getId())));

        visitedEdges.clear();
        for(CyEdge cyEdge : cyNetwork.getEdgeList()) {
            if(edgeExistsInGraph(cyEdge)) {
                visit(cyEdge);
                commandBuilder.updateEdge(refId(cyEdge.getSource()), refId(cyEdge.getTarget()), properties(cyEdge));
            } else {
                PropertyKey<Long> startId = nodeId(cyEdge.getSource());
                PropertyKey<Long> endId = nodeId(cyEdge.getTarget());
                commandBuilder.addEdge(startId, endId, properties(cyEdge));
            }
        }
        unvisitedEdges().forEach(removeEdge -> commandBuilder.removeEdge(new PropertyKey<>("id",removeEdge.getId())));
        return commandBuilder.sort(this::compareCommands).build();
    }

    private int compareCommands(Command command1, Command command2) {
        return Integer.compare(arity(command1), arity(command2));
    }
    private int arity(Command command) {
        if(command instanceof RemoveNode) return 0;
        if(command instanceof AddNode) return 1;
        if(command instanceof AddEdge) return 2;
        if(command instanceof UpdateNode) return 3;
        if(command instanceof UpdateEdge) return 4;
        if(command instanceof RemoveEdge) return 5;
        else return 6;
    }

    private Stream<GraphEdge> unvisitedEdges() {
        return graph.edges().stream().filter(edge -> !visitedEdges.contains(edge));
    }

    private Stream<GraphNode> unvisitedNodes() {
        return graph.nodes().stream().filter(node -> !visitedNode.contains(node));
    }

    private void visit(CyNode cyNode) {
        GraphNode node = graph
                .getNodeById(cyNetwork.getRow(cyNode).get(REFID,Long.class))
                .orElseThrow(() -> new IllegalStateException("Could not find node for cyNode SUID: " + cyNode.getSUID()));
        visitedNode.add(node);
    }

    private void visit(CyEdge cyEdge) {
        GraphEdge edge = graph
                .getEdgeById(cyNetwork.getRow(cyEdge).get(REFID,Long.class))
                .orElseThrow(() -> new IllegalStateException("Could not find edge for cyEdge SUID: " + cyEdge.getSUID()));
        visitedEdges.add(edge);
    }

    private PropertyKey<Long>  nodeId(CyNode node) {
        if(nodeExistsInGraph(node))  {
            return refId(node);
        } else {
            return suid(node);
        }
    }

    private PropertyKey<Long> refId(CyNode node) {
        return new PropertyKey<>(REFID, cyNetwork.getRow(node).get(REFID, Long.class));
    }

    private PropertyKey<Long> suid(CyNode node) {
        return new PropertyKey<>(SUID, node.getSUID());
    }

    private Map<String,Object> properties(CyIdentifiable cyIdentifiable) {
        CyRow cyRow = cyNetwork.getRow(cyIdentifiable);
        return cyRow.getAllValues();
    }

    private List<NodeLabel> labels(CyNode cyNode) {
        CyRow cyRow = cyNetwork.getRow(cyNode);
        if(cyRow.isSet("_neo4jlabels")) {
            return cyRow.getList("_neo4jlabels", String.class).stream()
                    .map(NodeLabel::create)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private boolean nodeExistsInGraph(CyNode cyNode) {
        return nodeExistsInGraph(cyNetwork.getRow(cyNode));
    }
    private boolean edgeExistsInGraph(CyEdge cyEdge) {
        return edgeExistsInGraph(cyNetwork.getRow(cyEdge));
    }

    private boolean nodeExistsInGraph(CyRow row) {
        return refId(row).map(graph::containsNodeId).orElse(false);
    }
    private boolean edgeExistsInGraph(CyRow row) {
        return refId(row).map(graph::containsNodeId).orElse(false);
    }

    private Optional<Long> refId(CyRow row) {
        if(row.isSet(REFID)) {
            return Optional.ofNullable(row.get(REFID, Long.class));
        } else {
            return Optional.empty();
        }
    }
}
