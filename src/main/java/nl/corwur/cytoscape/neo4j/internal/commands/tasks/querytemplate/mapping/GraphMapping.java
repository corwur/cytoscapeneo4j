package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping;

import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.GraphMappingImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph.ImportGraphStrategy;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.values.ValueExpression;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a mapping strategy from a graph to Cytoscape
 */
public class GraphMapping implements MappingStrategy {

    private final String nodeReferenceIdColumn;
    private final String edgeReferenceIdColumn;
    private List<NodeColumnMapping> nodeColumnMapping;
    private List<EdgeColumnMapping> edgeColumnMapping;

    public GraphMapping(
            List<NodeColumnMapping> nodeColumnMapping,
            List<EdgeColumnMapping> edgeColumnMapping,
            String nodeReferenceIdColumn,
            String edgeReferenceIdColumn) {
        this.nodeColumnMapping = nodeColumnMapping;
        this.edgeColumnMapping = edgeColumnMapping;
        this.nodeReferenceIdColumn = nodeReferenceIdColumn;
        this.edgeReferenceIdColumn = edgeReferenceIdColumn;
    }

    public List<NodeColumnMapping> getNodeColumnMapping() {
        return nodeColumnMapping;
    }

    public List<EdgeColumnMapping> getEdgeColumnMapping() {
        return edgeColumnMapping;
    }

    public String getNodeReferenceIdColumn() {
        return nodeReferenceIdColumn;
    }

    public String getEdgeReferenceIdColumn() {
        return edgeReferenceIdColumn;
    }


    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void accept(MappingStrategyVisitor visitor) {
        visitor.visit(this);
    }

    public static class Builder {

        private List<NodeColumnMapping> nodeColumnMapping = new ArrayList<>();
        private List<EdgeColumnMapping> edgeColumnMapping = new ArrayList<>();
        private String nodeReferenceIdColumn;
        private String edgeReferenceIdColumn;


        public <T> Builder addNodeColumnMapping(String columnName, Class<T> type, ValueExpression<GraphNode, T> valueExpression) {
            nodeColumnMapping.add(new NodeColumnMapping(columnName, type, valueExpression));
            return this;
        }

        public <T> Builder addEdgeColumnMapping(String columnName, Class<T> type, ValueExpression<GraphEdge, T> valueExpression) {
            edgeColumnMapping.add(new EdgeColumnMapping(columnName, type, valueExpression));
            return this;
        }

        public Builder setNodeReferenceIdColumn(String nodeReferenceIdColumn) {
            this.nodeReferenceIdColumn = nodeReferenceIdColumn;
            return this;
        }
        public Builder setEdgeReferenceIdColumn(String edgeReferenceIdColumn) {
            this.edgeReferenceIdColumn = edgeReferenceIdColumn;
            return this;
        }

        public GraphMapping build() {
            return new GraphMapping(
                    nodeColumnMapping,
                    edgeColumnMapping,
                    nodeReferenceIdColumn,
                    edgeReferenceIdColumn
            );
        }
    }
}
