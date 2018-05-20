package nl.corwur.cytoscape.neo4j.internal.graph.commands.p1;

import java.util.List;
import java.util.Map;

public interface GraphImplementation {

    void addEdge(PropertyKey<Long> start, PropertyKey<Long> end, String relationship, Map<String, Object> properties) throws GraphImplementationException;
    void addNode(List<NodeLabel> labels, Map<String, Object> properties) throws GraphImplementationException;
    void removeEdge(PropertyKey<Long> edgeId) throws GraphImplementationException;
    void updateEdge(PropertyKey<Long> startId, PropertyKey<Long> endId, Map<String, Object> properties) throws GraphImplementationException;
    void removeAllEdgesFromNode(NamedParameterProperty<Long> node) throws GraphImplementationException, GraphImplementationException;
    void removeNode(PropertyKey<Long> node) throws GraphImplementationException;
    void updateNode(PropertyKey<Long> nodeId, List<NodeLabel> labels, Map<String, Object> properties) throws GraphImplementationException;

    class ParameterProperty<T> {
        final String parameter;
        final T value;
        public ParameterProperty(String parameter, T value) {
            this.parameter = parameter;
            this.value = value;
        }

        public String getParameter() {
            return parameter;
        }

        public T getValue() {
            return value;
        }
    }

    final class NamedParameterProperty<T> extends ParameterProperty<T>{
        final String name;
        public NamedParameterProperty(String name, String parameter, T value) {
            super(parameter, value);
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
