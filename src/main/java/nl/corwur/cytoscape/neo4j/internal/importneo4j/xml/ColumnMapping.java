package nl.corwur.cytoscape.neo4j.internal.importneo4j.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
class ColumnMapping implements Mapping {

    @XmlElement(name = "node")
    private NodeMapping nodeMapping;

    @XmlElement(name = "edge")
    private EdgeMapping edgeMapping;

    public NodeMapping getNodeMapping() {
        return nodeMapping;
    }

    public EdgeMapping getEdgeMapping() {
        return edgeMapping;
    }


    public void setNodeMapping(NodeMapping nodeMapping) {
        this.nodeMapping = nodeMapping;
    }

    public void setEdgeMapping(EdgeMapping edgeMapping) {
        this.edgeMapping = edgeMapping;
    }

    @Override

    public void accept(MappingVisitor visitor) {
        visitor.visit(this);
    }
}
