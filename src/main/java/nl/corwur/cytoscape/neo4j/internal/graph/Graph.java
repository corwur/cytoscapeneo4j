package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Represents a graph
 */
public class Graph implements GraphObject {

    private final Map<Long, GraphNode> nodeTable = new HashMap<>();
    private final Map<Long, GraphEdge> edgeTable = new HashMap<>();

    public static Graph empty() {
        return new Graph();
    }

    public static Graph createFrom(List<GraphObject> list) {
        GraphBuilder graphBuilder = new GraphBuilder();
        list.forEach(o -> o.accept(graphBuilder));
        return graphBuilder.getGraph();
    }

    public Optional<GraphNode> getNodeById(long id) {
        return Optional.ofNullable(nodeTable.get(id));
    }

    public Optional<GraphEdge> getEdgeById(long id) {
        return Optional.ofNullable(edgeTable.get(id));
    }

    public Collection<GraphNode> nodes() {
        return Collections.unmodifiableCollection(nodeTable.values());
    }

    public Collection<GraphEdge> edges() {
        return Collections.unmodifiableCollection(edgeTable.values());
    }

    /**
     * Add a new node, if the node already exists merge the node.
     *
     * @param node
     */
    public void add(GraphNode node) {
        nodeTable.compute(node.getId(), merge(node));
    }

    /**
     * Add a new edge, if the edge already exists merge the edge. If the start or end node does not exists create a new node.
     *
     * @param edge
     */

    public void add(GraphEdge edge) {
        edgeTable.compute(edge.getId(), merge(edge));
        nodeTable.putIfAbsent(edge.getStart(), new GraphNode(edge.getStart()));
        nodeTable.putIfAbsent(edge.getStart(), new GraphNode(edge.getEnd()));
    }

    /**
     * @return the number of nodes + the number of edges in the graph
     */
    public int size() {
        return nodeTable.size() + edgeTable.size();
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    private BiFunction<Long, GraphNode, GraphNode> merge(GraphNode node) {
        return merge(oldValue -> oldValue.merge(node), node);
    }

    private BiFunction<Long, GraphEdge, GraphEdge> merge(GraphEdge edge) {
        return merge(oldValue -> oldValue.merge(edge), edge);
    }

    private <T> BiFunction<Long, T, T> merge(Function<T, T> merge, T defaultValue) {
        return (id, oldValue) -> Optional.ofNullable(oldValue)
                .map(value -> merge.apply(value))
                .orElse(defaultValue);
    }

    public boolean containsNodeId(long nodeId) {
        return nodeTable.containsKey(nodeId);
    }

    public boolean edgeExists(long start, long end) {
        return edges().stream().anyMatch(edge -> edge.getStart() == start && edge.getEnd() == end);
    }

    public Optional<GraphEdge> getEdge(long start, long end) {
        return edges().stream().filter(edge -> edge.getStart() == start && edge.getEnd() == end).findFirst();
    }

    public boolean containsEdgeId(long edgeId) {
        return edgeTable.containsKey(edgeId);
    }

    public GraphNode createNode() {
        long id = nodes().stream().max((n1, n2) -> Long.compare(n1.getId(), n2.getId())).map(GraphNode::getId).orElse(1l);
        return new GraphNode(id);
    }

    public GraphEdge createEdge(Predicate<GraphNode> selectA, Predicate<GraphNode> selectB) {
        GraphNode a = nodes().stream().filter(selectA).findFirst().orElseThrow(() -> new IllegalArgumentException("Could not a find node for predicate"));
        GraphNode b = nodes().stream().filter(selectB).findFirst().orElseThrow(() -> new IllegalArgumentException("Could not a find node for predicate"));
        long id = edges().stream().max((e1, e2) -> Long.compare(e1.getId(), e2.getId())).map(GraphEdge::getId).orElse(1l);
        return new GraphEdge(id, a.getId(), b.getId());
    }

    private static final class GraphBuilder implements GraphVisitor {

        private Graph graph = new Graph();

        public Graph getGraph() {
            return graph;
        }

        @Override
        public void visit(GraphNode graphNode) {
            graph.add(graphNode);
        }

        @Override
        public void visit(GraphEdge graphEdge) {
            graph.add(graphEdge);
        }

        @Override
        public void visit(GraphResult graphResult) {
            graphResult.getAll().forEach(o -> o.accept(this));
        }

        @Override
        public void visit(GraphLong neo4jLong) {

        }

        @Override
        public void visit(GraphUnspecifiedType neo4jUnspecifiedType) {

        }

        @Override
        public void visit(GraphPath graphPath) {
            graphPath.getNodes().forEach(n -> n.accept(this));
            graphPath.getEdges().forEach(e -> e.accept(this));
        }

        @Override
        public void visit(GraphObjectList graphObjectList) {
            graphObjectList.getList().forEach(o -> o.accept(this));
        }

        @Override
        public void visit(Graph graph) {
            graph.nodes().forEach(n -> n.accept(this));
            graph.edges().forEach(e -> e.accept(this));
        }
    }
}
