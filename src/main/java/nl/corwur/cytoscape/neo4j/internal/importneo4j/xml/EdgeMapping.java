package nl.corwur.cytoscape.neo4j.internal.importneo4j.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
class EdgeMapping {

    @XmlAttribute(name="referenceIdColumn")
    private String referenceIdColumn;

    @XmlElement(name = "column")
    private List<EdgeColumn> columnList;

    public void setReferenceIdColumn(String referenceIdColumn) {
        this.referenceIdColumn = referenceIdColumn;
    }

    public void setColumnList(List<EdgeColumn> columnList) {
        this.columnList = columnList;
    }

    public List<EdgeColumn> getColumnList() {
        return columnList;
    }

    public String getReferenceIdColumn() {
        return referenceIdColumn;
    }
}
