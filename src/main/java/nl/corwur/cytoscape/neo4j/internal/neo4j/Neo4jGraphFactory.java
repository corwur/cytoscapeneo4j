package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphLong;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObject;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObjectList;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphPath;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphResult;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphUnspecifiedType;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.neo4j.driver.util.Pair;

import java.util.List;

class Neo4jGraphFactory {

    GraphObject create(Record record) {
        GraphResult neo4jResult = new GraphResult();
        for (Pair<String, Value> pair : record.fields()) {
            neo4jResult.add(pair.key(), create(pair.value()));
        }
        return neo4jResult;
    }

    private GraphObject create(Value value) {
        switch (value.type().name()) {
            case "NODE":
                return create(value.asNode());
            case "RELATIONSHIP":
                return create(value.asRelationship());
            case "PATH":
                return create(value.asPath());
            case "BOOLEAN":
                return create(value.asRelationship());
            case "INTEGER":
                return create(value.asLong());
            case "LIST OF ANY?":
                return create(value.asList());
            default:
                return new GraphUnspecifiedType();
        }
    }

    private GraphObject create(List<Object> objects) {
        return objects.stream()
                .filter(o -> o instanceof Entity)
                .map(o -> this.create((Entity) o))
                .collect(GraphObjectList::new, (list, o) -> list.add(o), (list1, list2) -> list1.addAll(list2));
    }

    private GraphObject create(Entity entity) {
        if (entity instanceof Relationship) {
            return create((Relationship) entity);
        } else if (entity instanceof Node) {
            return create((Node) entity);
        }
        throw new IllegalStateException();
    }

    private GraphObject create(Path path) {

        GraphPath graphPath = new GraphPath();
        for (Node node : path.nodes()) {
            graphPath.add(create(node));
        }
        for (Relationship relationship : path.relationships()) {
            graphPath.add(create(relationship));
        }
        return graphPath;
    }

    private GraphLong create(long value) {
        return new GraphLong(value);
    }

    private GraphEdge create(Relationship relationship) {
        GraphEdge graphEdge = new GraphEdge();
        graphEdge.setStart(relationship.startNodeId());
        graphEdge.setEnd(relationship.endNodeId());
        graphEdge.setProperties(relationship.asMap());
        graphEdge.setType(relationship.type());
        graphEdge.setId(relationship.id());
        return graphEdge;
    }

    private GraphNode create(org.neo4j.driver.types.Node node) {
        GraphNode graphNode = new GraphNode(node.id());
        graphNode.setProperties(node.asMap());
        for (String label : node.labels()) {
            graphNode.addLabel(label);
        }
        return graphNode;
    }
}
