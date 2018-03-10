package nl.corwur.cytoscape.neo4j.internal.commands.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddEdgeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddNodeCommand;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClientException;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

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
            AddEdgeCommand cmd = new AddEdgeCommand();
            CyRow cyRow=cyNetwork.getDefaultNodeTable().getRow(cyEdge.getSUID());
            cmd.setEdgeProperties(cyRow.getAllValues());
            cmd.setStartId(cyEdge.getSource().getSUID());
            cmd.setEndId(cyEdge.getTarget().getSUID());
            cmd.setRelationship(relationship);
            cmd.setDirected(cyEdge.isDirected());
            cmd.setNodeLabel(exportNetworkConfiguration.getNodeLabel());
            services.getNeo4jClient().executeCommand(cmd);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException("Error copying to Neo4j", e);
        }
    }

    private void copyNodeToNeo4j(CyNetwork cyNetwork, CyNode cyNode)  {
        try {
            AddNodeCommand cmd = new AddNodeCommand();
            cmd.setNodeLabel(exportNetworkConfiguration.getNodeLabel());
            CyRow cyRow = cyNetwork.getDefaultNodeTable().getRow(cyNode.getSUID());
            cmd.setNodeProperties(cyRow.getAllValues());
            cmd.setNodeId(cyNode.getSUID());
            services.getNeo4jClient().executeCommand(cmd);
        } catch (Neo4jClientException e) {
            throw new IllegalStateException("Error copying to Neo4j", e);
        }
    }
}
