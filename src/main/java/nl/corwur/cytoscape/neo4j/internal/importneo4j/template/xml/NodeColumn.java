package nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class NodeColumn {

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "property")
    Property property;

    @XmlElement(name = "id")
    Id id;

    @XmlElement(name = "expression")
    Expression expression;

    @XmlElement(name = "label")
    Label label;

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

    public Expression getExpression() {
        return expression;
    }

    public Label getLabel() {
        return label;
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

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
