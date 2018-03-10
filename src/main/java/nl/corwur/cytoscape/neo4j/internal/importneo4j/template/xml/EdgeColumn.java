package nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EdgeColumn {

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "property")
    private Property property;

    @XmlElement(name = "id")
    private Id id;

    @XmlElement(name = "edgetype")
    private EdgeType edgeType;

    @XmlElement(name = "expression")
    private Expression expression;

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Property getProperty() {
        return property;
    }

    public Id getId() {
        return id;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void setId(Id id) {
        this.id = id;
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }
}
