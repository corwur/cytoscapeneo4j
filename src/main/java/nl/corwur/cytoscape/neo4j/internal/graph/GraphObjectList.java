package nl.corwur.cytoscape.neo4j.internal.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a list of objects that are art of a graph.
 */
public class GraphObjectList implements GraphObject {

    private List<GraphObject> list = new ArrayList<>();

    public void add(GraphObject graphObject) {
        list.add(graphObject);
    }

    public List<GraphObject> getList() {
        return Collections.unmodifiableList(list);
    }

    @Override
    public void accept(GraphVisitor graphVisitor) {
        graphVisitor.visit(this);
    }

    public void addAll(GraphObjectList that) {
        this.list.addAll(that.list);
    }
}
