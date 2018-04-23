package nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.EdgeColumnMapping;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.GraphMapping;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.NodeColumnMapping;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import org.cytoscape.model.*;

import java.util.Optional;

/**
 * This class implements an import strategy that uses a grpah mapping (used by query templates)
 * for copying nodes and edges into cytoscape.
 */
public class GraphMappingImportStrategy implements ImportGraphStrategy {

    private final GraphMapping graphMapping;

    public GraphMappingImportStrategy(GraphMapping graphMapping) {
        this.graphMapping = graphMapping;
    }

    @Override
    public void createTables(CyNetwork network) {
        CyTable nodeTable = network.getDefaultNodeTable();
        CyTable edgeTable = network.getDefaultEdgeTable();

        for(NodeColumnMapping<?> nodeColumnMapping : graphMapping.getNodeColumnMapping()) {
            if (!columnExists(nodeTable, nodeColumnMapping.getColumnName())) {
                nodeTable.createColumn(nodeColumnMapping.getColumnName(), nodeColumnMapping.getColumnType(), true);
            }
        }
        for(EdgeColumnMapping<?> edgeColumnMapping : graphMapping.getEdgeColumnMapping()) {
            if (!columnExists(edgeTable, edgeColumnMapping.getColumnName())) {
                edgeTable.createColumn(edgeColumnMapping.getColumnName(), edgeColumnMapping.getColumnType(), true);
            }
        }
    }

    private boolean columnExists(CyTable cyTable, String columnName) {
        return cyTable.getColumns().stream().anyMatch(cyColumn -> cyColumn.getName().equals(columnName));
    }

    @Override
    public void copyNode(CyNetwork network, GraphNode graphNode) {
        CyNode cyNode = getNodeByIdOrElseCreate(network, graphNode.getId());
        CyRow cyRow = network.getRow(cyNode);
        for(NodeColumnMapping<?> nodeColumnMapping : graphMapping.getNodeColumnMapping()) {
            cyRow.set(nodeColumnMapping.getColumnName(), nodeColumnMapping.getValueExpression().eval(graphNode));
        }
    }

    @Override
    public void copyEdge(CyNetwork network, GraphEdge graphEdge) {

        if(edgeExists(network, graphEdge.getId())) {
            return;
        }
        Long start = graphEdge.getStart();
        Long end = graphEdge.getEnd();

        CyNode startNode = getNodeByIdOrElseCreate(network, start);
        CyNode endNode = getNodeByIdOrElseCreate(network, end);
        CyEdge cyEdge = network.addEdge(startNode, endNode, true);
        CyRow cyRow = network.getRow(cyEdge);

        for(EdgeColumnMapping<?> edgeColumnMapping : graphMapping.getEdgeColumnMapping()) {
            cyRow.set(edgeColumnMapping.getColumnName(), edgeColumnMapping.getValueExpression().eval(graphEdge));
        }
    }

    @Override
    public void postProcess(CyNetwork network) {

    }

    private boolean edgeExists(CyNetwork network, long id) {
        String edgeRefereceIdColumn = graphMapping.getEdgeReferenceIdColumn();
        return ! network
                .getDefaultEdgeTable()
                .getMatchingRows(edgeRefereceIdColumn , id)
                .isEmpty();
    }

    private CyNode getNodeByIdOrElseCreate(CyNetwork currNet, Long id) {
        return getNodeById(currNet, id).orElseGet(() -> createNewNode(currNet, id));
    }

    private Optional<CyNode> getNodeById(CyNetwork network, Long id) {
        String primaryKeyColumnName =network.getDefaultNodeTable().getPrimaryKey().getName();
        String nodeReferenceIdColumn = graphMapping.getNodeReferenceIdColumn();
        return network
                .getDefaultNodeTable()
                .getMatchingRows(nodeReferenceIdColumn, id)
                .stream()
                .findFirst()
                .map(row -> network.getNode(row.get(primaryKeyColumnName, Long.class)));
    }

    private CyNode createNewNode(CyNetwork currNet, Long id) {
        String nodeReferenceIdColumn = graphMapping.getNodeReferenceIdColumn();
        CyNode newNode = currNet.addNode();
        currNet.getRow(newNode).set(nodeReferenceIdColumn, id);
        return newNode;
    }
}
