package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.*;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Pair;

class Neo4jGraphFactory {

    GraphObject create(Record record) {
        GraphResult neo4jResult = new GraphResult();
        for(Pair<String, Value> pair : record.fields()) {
                neo4jResult.add(pair.key(), create(pair.value()));
        }
        return neo4jResult;
    }

    private GraphObject create(Value value) {
        switch (value.type().name()) {
            case "NODE" : return create(value.asNode());
            case "RELATIONSHIP" : return create(value.asRelationship());
            case "PATH" : return create(value.asPath());
            case "BOOLEAN" : return create(value.asRelationship());
            case "INTEGER" : return create(value.asLong());
        }
        return new GraphUnspecifiedType();
    }

    private GraphObject create(Path path) {

        GraphPath graphPath = new GraphPath();
        for(Node node : path.nodes()) {
            graphPath.add(create(node));
        }
        for(Relationship relationship : path.relationships()) {
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

    private GraphNode create(org.neo4j.driver.v1.types.Node node) {
        GraphNode graphNode = new GraphNode();
        graphNode.setProperties(node.asMap());
        for(String label :node.labels()) {
            graphNode.addLabel(label);
        }
        graphNode.setId(node.id());
        return graphNode;
    }
}
