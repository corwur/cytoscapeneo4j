package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.template.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cytemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryTemplate {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "query")
    private String query;

    @XmlElement(name = "parameters")
    private Parameters parameters;

    @XmlElements(value = {
            @XmlElement(name = "mapping", type = ColumnMapping.class),
            @XmlElement(name = "copyall", type = CopyAll.class)
    })
    private Mapping mapping;

    public String getQuery() {
        return query;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public Mapping getMapping() {
        return mapping;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }
}
