package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import java.util.Map;

/**
 * This class implements the 'Add Edge' command that can be executed by a Neo4jClient.
 */
public class AddEdgeCommand extends Command {
    private Map<String, Object> edgeProperties;
    private Long startId;
    private Long endId;
    private boolean directed;
    private String relationship;
    private Label nodeLabel;
    private String relationshipName;
    private String getEdgePropertiesName;
    private String edgePropertiesName;
    private String startNodeIdProperty;
    private String startNodeIdParameter;
    private String endNodeIdProperty;
    private String endNodeIdParameter;

    public void setEdgeProperties(Map<String, Object> edgeProperties) {
        this.edgeProperties = edgeProperties;
    }

    public Map<String, Object> getEdgeProperties() {
        return edgeProperties;
    }

    public void setStartId(Long startId) {
        this.startId = startId;
    }

    public Long getStartId() {
        return startId;
    }

    public void setEndId(Long endId) {
        this.endId = endId;
    }

    public Long getEndId() {
        return endId;
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
    }

    public boolean isDirected() {
        return directed;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }

    public Label getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(Label nodeLabel) {
        this.nodeLabel = nodeLabel;
    }


    public String getRelationshipName() {
        return relationshipName;
    }

    public String getEdgePropertiesName() {
        return edgePropertiesName;
    }

    public void setEdgePropertiesName(String edgePropertiesName) {
        this.edgePropertiesName = edgePropertiesName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    public String getGetEdgePropertiesName() {
        return getEdgePropertiesName;
    }

    public void setGetEdgePropertiesName(String getEdgePropertiesName) {
        this.getEdgePropertiesName = getEdgePropertiesName;
    }

    public String getStartNodeIdProperty() {
        return startNodeIdProperty;
    }

    public void setStartNodeIdProperty(String startNodeIdProperty) {
        this.startNodeIdProperty = startNodeIdProperty;
    }

    public String getStartNodeIdParameter() {
        return startNodeIdParameter;
    }

    public void setStartNodeIdParameter(String startNodeIdParameter) {
        this.startNodeIdParameter = startNodeIdParameter;
    }

    public String getEndNodeIdProperty() {
        return endNodeIdProperty;
    }

    public void setEndNodeIdProperty(String endNodeIdProperty) {
        this.endNodeIdProperty = endNodeIdProperty;
    }

    public String getEndNodeIdParameter() {
        return endNodeIdParameter;
    }

    public void setEndNodeIdParameter(String endNodeIdParameter) {
        this.endNodeIdParameter = endNodeIdParameter;
    }

    @Override
    public void execute() {

    }
}
