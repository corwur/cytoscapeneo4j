package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * This class represents an edge in a graph.
 */
public class GraphEdge implements GraphObject {
    private long start;
    private long end;
    private Map<String, Object> properties = new ConcurrentHashMap<>();
    private String type;
    private long id;

    public GraphEdge() {}

    public GraphEdge(long id, long start, long end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStart() {
        return start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getEnd() {
        return end;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
    }

    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
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


    GraphEdge merge(GraphEdge that) {
        this.properties.putAll(that.properties.keySet()
                    .stream()
                    .filter(key -> !this.properties.containsKey(key))
                    .collect(Collectors.toMap(key -> key, key -> that.properties.get(key)))
            );
        if(that.type != null) {
            this.type = type;
        }
        return this;
    }
}
