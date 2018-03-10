package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a ditionary withgraph objects that are part of a graph.
 */
public class GraphResult implements GraphObject {

    private Map<String, GraphObject> results = new HashMap<>();

    public void add(String key, GraphObject graphObject) {
        results.put(key, graphObject);
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    public GraphObject get(String key) {
        return results.get(key);
    }

    public Collection<GraphObject> getAll() {
        return Collections.unmodifiableCollection(results.values());
    }
}
