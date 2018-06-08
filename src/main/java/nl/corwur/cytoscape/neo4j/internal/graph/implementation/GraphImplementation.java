package nl.corwur.cytoscape.neo4j.internal.graph.implementation;

import java.util.List;
import java.util.Map;

public interface GraphImplementation {

    void addEdge(PropertyKey<Long> start, PropertyKey<Long> end, String relationship, Map<String, Object> properties) throws GraphImplementationException;

    void addNode(List<NodeLabel> labels, Map<String, Object> properties) throws GraphImplementationException;

    void removeEdge(PropertyKey<Long> edgeId) throws GraphImplementationException;

    void updateDirectedEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) throws GraphImplementationException;

    void removeAllEdgesFromNode(PropertyKey<Long> nodeId) throws GraphImplementationException;

    void removeNode(PropertyKey<Long> node) throws GraphImplementationException;

    void updateNode(PropertyKey<Long> nodeId, List<NodeLabel> labels, Map<String, Object> properties) throws GraphImplementationException;

    void updateEdgeById(PropertyKey<Long> edgeId, Map<String, Object> properties) throws GraphImplementationException;
}
