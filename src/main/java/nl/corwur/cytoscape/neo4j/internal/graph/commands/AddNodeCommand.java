package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import java.util.List;
import java.util.Map;

/**
 * This class implements the 'Add Node' command that can be executed by a Neo4jClient.
 */

public class AddNodeCommand {
    private Map<String, Object> nodeProperties;
    private Long nodeId;
    private Label label = null;
    private String nodeIdPropertyName;
    private String nodePropertiesName;
    private List<NodeLabel> nodeLabelList;
    private String nodeName;

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
    
    public Label getNodeLabel(){
    	return label;
    }
    public void setNodeLabel(Label label) {
    	this.label = label;
    }

    public String getNodeIdPropertyName() {
        return nodeIdPropertyName;
    }

    public void setNodeIdPropertyName(String nodeIdPropertyName) {
        this.nodeIdPropertyName = nodeIdPropertyName;
    }

    public String getNodePropertiesName() {
        return nodePropertiesName;
    }

    public void setNodePropertiesName(String nodePropertiesName) {
        this.nodePropertiesName = nodePropertiesName;
    }

    public void setLabel(NodeLabel label) {
        this.label = label;
    }

    public void setNodeLabelList(List<NodeLabel> nodeLabelList) {
        this.nodeLabelList = nodeLabelList;
    }

    public List<NodeLabel> getNodeLabelList() {
        return nodeLabelList;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeName() {
        return nodeName;
    }
}
