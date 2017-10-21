package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.Map;
import java.util.Optional;

public class GraphEdge extends GraphObject {
    private long start;
    private long end;
    private Map<String, Object> properties;
    private String type;
    private long id;

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
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return properties;
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


}
