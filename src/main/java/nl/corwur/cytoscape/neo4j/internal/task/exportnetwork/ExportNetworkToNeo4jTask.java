package nl.corwur.cytoscape.neo4j.internal.task.exportnetwork;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.graph.AddEdgeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.AddNodeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class ExportNetworkToNeo4jTask extends AbstractTask {

    private final Services services;
    private final NodeLabel nodeLabel;

    public ExportNetworkToNeo4jTask(Services services, NodeLabel nodeLabel) {
        this.services = services;
        this.nodeLabel = nodeLabel;
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
                cyNetwork.getNodeList().forEach(node -> {
                    try {
                        copyNodeToNeo4j(cyNetwork, node);
                    } catch (Neo4jClientException e) {
                        throw new IllegalStateException("Error copying to Neo4j", e);
                    }
                });
                taskMonitor.setStatusMessage("Exporting edges");
                cyNetwork.getEdgeList().forEach(edge -> {
                    try {
                        copyEdgeToNeo4j(cyNetwork, edge);
                    } catch (Neo4jClientException e) {
                        throw new IllegalStateException("Error copying to Neo4j", e);
                    }
                });
            }

        } catch (Exception e) {
            taskMonitor.showMessage(TaskMonitor.Level.ERROR, e.getMessage());
        }
    }

    private void copyEdgeToNeo4j(CyNetwork cyNetwork, CyEdge cyEdge) throws Neo4jClientException {
        AddEdgeCommand cmd = new AddEdgeCommand();
        CyRow cyRow=cyNetwork.getDefaultNodeTable().getRow(cyEdge.getSUID());
        cmd.setEdgeProperties(cyRow.getAllValues());
        cmd.setStartId(cyEdge.getSource().getSUID());
        cmd.setEndId(cyEdge.getTarget().getSUID());
        cmd.setRelationship("links");
        cmd.setDirected(cyEdge.isDirected());
        cmd.setNodeLabel(nodeLabel);
        services.getNeo4jClient().executeCommand(cmd);
    }

    private void copyNodeToNeo4j(CyNetwork cyNetwork, CyNode cyNode) throws Neo4jClientException {
        AddNodeCommand cmd = new AddNodeCommand();
        cmd.setNodeLabel(nodeLabel);
        CyRow cyRow=cyNetwork.getDefaultNodeTable().getRow(cyNode.getSUID());
        cmd.setNodeProperties(cyRow.getAllValues());
        cmd.setNodeId(cyNode.getSUID());
        services.getNeo4jClient().executeCommand(cmd);
    }
}
