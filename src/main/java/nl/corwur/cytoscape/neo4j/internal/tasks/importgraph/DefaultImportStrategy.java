package nl.corwur.cytoscape.neo4j.internal.commands.tasks.importgraph;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObjectList;
import org.cytoscape.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements an import strategy that copies all labels and properties to cytoscape
 */
public class DefaultImportStrategy implements ImportGraphStrategy {

    private static final String COLUMN_REFERENCEID = "refid";
    private static final String COLUMN_NAME = "name";

    private Map<Long, Long> idMap = new HashMap<>();
    private Map<Long, GraphNode> nodeMap = new HashMap<>();
    private List<GraphEdge> edges = new ArrayList<>();

    @Override
    public void postProcess(CyNetwork network) {
        nodeMap.values().stream().forEach(graphNode -> saveNode(network, graphNode));
        edges.forEach(graphEdge -> saveEdge(network, graphEdge));
    }


    @Override
    public void createTables(CyNetwork network) {
        CyTable defNodeTab = network.getDefaultNodeTable();
        if (defNodeTab.getColumn(COLUMN_REFERENCEID) == null) {
            defNodeTab.createColumn(COLUMN_REFERENCEID, Long.class, false);
        }
        if (defNodeTab.getColumn(COLUMN_NAME) == null) {
            defNodeTab.createColumn(COLUMN_NAME, String.class, false);
        }

        CyTable defEdgeTab = network.getDefaultEdgeTable();
        if (defEdgeTab.getColumn(COLUMN_REFERENCEID) == null) {
            defEdgeTab.createColumn(COLUMN_REFERENCEID, Long.class, false);
        }
        if (defEdgeTab.getColumn(COLUMN_NAME) == null) {
            defEdgeTab.createColumn(COLUMN_NAME, String.class, false);
        }
    }


    public void copyNode(CyNetwork network, GraphNode graphNode) {
        long nodeId = Long.valueOf(graphNode.getProperties().getOrDefault(COLUMN_REFERENCEID, graphNode.getId()).toString());
        idMap.put(graphNode.getId(), nodeId);
        nodeMap.putIfAbsent(nodeId, graphNode);
    }

    public void copyEdge(CyNetwork network, GraphEdge graphEdge) {
        edges.add(graphEdge);
    }

    private void saveNode(CyNetwork network, GraphNode graphNode) {

        long nodeId = Long.valueOf(graphNode.getProperties().getOrDefault(COLUMN_REFERENCEID, graphNode.getId()).toString());
        if(nodeExists(network, nodeId)) {
            return;
        }

        CyTable cyTable = network.getDefaultNodeTable();
        CyNode cyNode = getOrCreateNode(network, nodeId);

        for (Map.Entry<String, Object> entry : graphNode.getProperties().entrySet()) {
            createColumn(cyTable, entry.getKey(), entry.getValue());
            setEntry(cyTable, cyNode, entry.getKey(),entry.getValue());
        }
        createColumn(cyTable, "_neo4j_labels", graphNode.getLabels());
        setEntry(cyTable, cyNode, "_neo4j_labels", graphNode.getLabels());
    }

    private boolean nodeExists(CyNetwork network, long nodeId) {
        return !network.getDefaultNodeTable().getMatchingRows(COLUMN_REFERENCEID, nodeId).isEmpty();
    }

    private void saveEdge(CyNetwork network, GraphEdge graphEdge) {
        long edgeId = Long.valueOf(graphEdge.getProperties().getOrDefault(COLUMN_REFERENCEID, graphEdge.getId()).toString());
        if(edgeExists(network, edgeId)) {
            return;
        }
        CyTable cyTable = network.getDefaultEdgeTable();

        Long start = idMap.getOrDefault(graphEdge.getStart(),graphEdge.getStart());
        Long end = idMap.getOrDefault(graphEdge.getEnd(), graphEdge.getEnd());
        String type = graphEdge.getType();

        CyNode startNode = getOrCreateNode(network, start);
        CyNode endNode = getOrCreateNode(network, end);

        CyEdge cyEdge = network.addEdge(startNode, endNode, true);

        network.getRow(cyEdge).set(COLUMN_REFERENCEID, edgeId);
        network.getRow(cyEdge).set(COLUMN_NAME, type);
        network.getRow(cyEdge).set(CyEdge.INTERACTION, type);

        for (Map.Entry<String, Object> entry : graphEdge.getProperties().entrySet()) {
            createColumn(cyTable, entry.getKey(), entry.getValue());
            setEntry(cyTable, cyEdge, entry.getKey(), entry.getValue());
        }
    }

    private boolean edgeExists(CyNetwork network, long id) {
        return !network.getDefaultEdgeTable().getMatchingRows(COLUMN_REFERENCEID, id).isEmpty();
    }

    private CyNode getOrCreateNode(CyNetwork network, long id) {
        CyColumn primaryKey =  network.getDefaultNodeTable().getPrimaryKey();
        return network.getDefaultNodeTable()
                .getMatchingRows(COLUMN_REFERENCEID, id)
                .stream()
                .findFirst()
                .map(cyRow -> network.getNode(cyRow.get(primaryKey.getName(), Long.class)))
                .orElseGet(() -> this.createNode(network, id));
    }

    private CyNode createNode(CyNetwork network, long id ) {
        CyNode cyNode = network.addNode();
        setEntry(network.getDefaultNodeTable(), cyNode, COLUMN_REFERENCEID, id);
        return cyNode;
    }

    private void createColumn(CyTable cyTable, String key, Object value) {
        if (cyTable.getColumn(key) == null) {
            if (value instanceof List) {
                cyTable.createListColumn(key, String.class, true);
            } else {
                cyTable.createColumn(key, value.getClass(), true);
            }
        }
    }

    private void setEntry(CyTable cyTable, CyIdentifiable cyId, String key, Object value) {
        cyTable.getRow(cyId.getSUID()).set(key, value);
    }
}
