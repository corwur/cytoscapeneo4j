package nl.corwur.cytoscape.neo4j.internal.importneo4j.template.xml;


import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

public class Parameters {

    @XmlElement(name="parameter")
    private List<Parameter> parameterList = new ArrayList<>();

    public List<Parameter> getParameterList() {
        return parameterList;
    }
}
