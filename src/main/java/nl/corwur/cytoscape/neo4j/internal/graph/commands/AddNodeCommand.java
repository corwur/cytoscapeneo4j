package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import java.util.Map;

/**
 * This class implements the 'Add Node' command that can be executed by a Neo4jClient.
 */

public class AddNodeCommand {
    private Map<String, Object> nodeProperties;
    private Long nodeId;
    private NodeLabel label = null;
    
    public void setNodeProperties(Map<String, Object> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public Map<String, Object> getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getNodeId() {
        return nodeId;
    }
    
    public NodeLabel getNodeLabel(){
    	return label;
    }
    public void setNodeLabel(NodeLabel label) {
    	this.label = label;
    }
}
