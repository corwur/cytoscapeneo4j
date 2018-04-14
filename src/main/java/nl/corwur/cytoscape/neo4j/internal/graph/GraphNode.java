package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class represents a node in a graph.
 */
public class GraphNode implements GraphObject {

    private Map<String, Object> properties = new HashMap<>();
    private List<String> labels = new ArrayList<>();
    private long id;

    public GraphNode(long id) {
        this.id = id;
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void addLabel(String label) {
        labels.add(label);
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public <T> Optional<T> getProperty(String key, Class<T> clz) {
        if (key != null && clz != null) {
            Object value = properties.get(key);
            if (value != null && clz.isInstance(value)) {
                return Optional.of(clz.cast(value));
            }
        }
        return Optional.empty();
    }

    public <T> Optional<T> getProperty(String key, Function<Object, T> fn) {
        if (key != null && fn != null) {
            Object value = properties.get(key);
            if (value != null) {
                return Optional.of(fn.apply(value));
            }
        }
        return Optional.empty();
    }

    public void ifLabelPresent(String label, Consumer<String> consumer) {
        labels.stream().filter(val -> val.equalsIgnoreCase(label)).findFirst().ifPresent(consumer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return id == graphNode.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    GraphNode merge(GraphNode that) {
        this.labels.addAll(
                that.labels.stream()
                        .filter(label -> !this.labels.contains(label))
                        .collect(Collectors.toList())
        );
        this.properties.putAll(that.properties.keySet()
                .stream()
                .filter(key -> !this.properties.containsKey(key))
                .collect(Collectors.toMap(key -> key, key -> that.properties.get(key)))
        );
        return this;
    }
}
