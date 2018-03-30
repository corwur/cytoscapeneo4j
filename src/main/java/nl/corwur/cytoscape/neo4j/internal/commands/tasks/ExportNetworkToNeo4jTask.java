package nl.corwur.cytoscape.neo4j.internal.commands.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddEdgeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddNodeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

import java.util.List;

/**
 * This class exports a cytoscape graph to neo4j.
 */
public class ExportNetworkToNeo4jTask extends AbstractTask {

    private final Services services;
    private final ExportNetworkConfiguration exportNetworkConfiguration;

    public ExportNetworkToNeo4jTask(Services services, ExportNetworkConfiguration exportNetworkConfiguration) {
        this.services = services;
        this.exportNetworkConfiguration = exportNetworkConfiguration;
    }

    @Override
    public void run(TaskMonitor taskMonitor) throws Exception {
        try {
            taskMonitor.setStatusMessage("Export network to Neo4j");
            CyNetwork cyNetwork = services.getCyApplicationManager().getCurrentNetwork();

            if(cyNetwork == null) {
                taskMonitor.showMessage(TaskMonitor.Level.WARN, "No network selected");
            } else {
                taskMonitor.setStatusMessage("Exporting nodes");
                cyNetwork.getNodeList().forEach(node -> copyNodeToNeo4j(cyNetwork, node));
                taskMonitor.setStatusMessage("Exporting edges");
                cyNetwork.getEdgeList().forEach(edge -> copyEdgeToNeo4j(cyNetwork, edge, exportNetworkConfiguration.getRelationship()));
            }

        } catch (Exception e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, e.getMessage());
        }
    }

    private void copyEdgeToNeo4j(CyNetwork cyNetwork, CyEdge cyEdge, String relationship)  {
        try {
            CyRow edgeRow = cyNetwork.getRow(cyEdge);

            long startId = exportNetworkConfiguration.getNodeReferenceId(cyEdge.getSource(),cyNetwork);
            long endId = exportNetworkConfiguration.getNodeReferenceId(cyEdge.getTarget(),cyNetwork);

            AddEdgeCommand cmd = new AddEdgeCommand();
            cmd.setStartId(startId);
            cmd.setEndId(endId);
            cmd.setRelationship(relationship);
            cmd.setRelationshipName("_neo4j_link");
            cmd.setEndNodeIdProperty(exportNetworkConfiguration.getNodeReferenceIdColumn());
            cmd.setEndNodeIdParameter("end_id");
            cmd.setStartNodeIdProperty(exportNetworkConfiguration.getNodeReferenceIdColumn());
            cmd.setStartNodeIdParameter("start_id");
            cmd.setDirected(cyEdge.isDirected());
            cmd.setEdgePropertiesName("_neo4j_edge_properties");
            cmd.setEdgeProperties(edgeRow.getAllValues());
            cmd.setNodeLabel(exportNetworkConfiguration.getNodeLabel());
            services.getNeo4jClient().executeCommand(cmd);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException("Error copying to Neo4j", e);
        }
    }

    private void copyNodeToNeo4j(CyNetwork cyNetwork, CyNode cyNode)  {
        try {

            List<NodeLabel> nodeLabelList = exportNetworkConfiguration.getNodeLabels(cyNode, cyNetwork);
            long nodeId = exportNetworkConfiguration.getNodeReferenceId(cyNode, cyNetwork);

            AddNodeCommand cmd = new AddNodeCommand();
            cmd.setNodeLabel(exportNetworkConfiguration.getNodeLabel());
            CyRow cyRow = cyNetwork.getRow(cyNode);
            cmd.setNodeProperties(cyRow.getAllValues());
            cmd.setNodePropertiesName(exportNetworkConfiguration.getNodePropertiesColumnName());
            cmd.setNodeId(nodeId);
            cmd.setNodeLabelList(nodeLabelList);
            cmd.setNodeIdPropertyName(exportNetworkConfiguration.getNodeReferenceIdColumn());
            cmd.setNodeName(exportNetworkConfiguration.getNodeName(cyNetwork, cyNode));
            services.getNeo4jClient().executeCommand(cmd);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException("Error copying to Neo4j", e);
        }
    }
}
