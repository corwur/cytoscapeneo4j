#Templates
The plugin supports parameterized cypher query templates and a configurable mapping. 
The template is specified in an xml file, the user can select the template in the user interface:
Apps -> Cypher Queries -> Import Stored Cypher Query.
#XML
The xml contains a query, parameters and a mapping specification. 
There are two mapping options, a default strategy 'copy all', it just copies all nodes and edges from Neo4j to cytoscape.
Or the user can specify an explicit mapping.

##cytemplate (1)
The root element for the temlae
###query (1)
Cyher query (typically in a CDATA element)
####parameters (0..1)
List of parameters
#####parameter (0..n)
A parameter has a name and a type, it is as a parameter in the cypher query.
Attributes:
- - name, the parameter name, string
- - type, a valid java type like (java.lang.String or java.lang.Integer, ...)
###mapping (0..1)
The mapping specifies how nodes and edge are imported in cytoscape. 
It contains two elements: node and edge.
####node (0..1)
Attributes:
- referenceIdColumn, the name of the column where the node id is copied into to.
#####column (0..n)
Attributes:
- type, a java type like java.lang.String 
- name, the name of the column the value is copied into
######expression (0..1)
The value of column is calculate by evaluating a javascript expression. 
The variable 'node' of type 'nl.corwur.cytoscape.neo4j.internal.graph.GraphNode' is passed into the expression.
######id (0..1)
The id of the Neo4j node is copied into the column
######property (0..1)
The property of the node is copied into the column.
Attributes:
- key, the name of the Neo4j property.
######label (0..1)
The label of the node is copied into the column.
Attributes:
- match, a regular expression that matches a label.
####edge (0..1)
An edge is mapped to columns.
#####column (0..n)
Attributes:
- type (java type like java.lang.String)
- name (string)
######id
Copy the id of the Neo4j edge into the column
######edgeType
Copy the id of the Neo4j edge into the column

###copyall
Use a copy all mapping strategy, all nodes and relations form the query are copied.
Attributes:
- referenceIdColumn (string), the name of the column where the id is stored.

##Example
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<cytemplate name="test-query">
    <query><![CDATA[MATCH p=(n:gene) - [r:order*] - (m:gene) WHERE n.geneID=$from AND m.geneID=$to RETURN p]]></query>
    <parameters>
        <parameter name="from" type="java.lang.String" />
        <parameter name="to" type="java.lang.String" />
    </parameters>
    <mapping>
        <node referenceIdColumn="refid">
            <column type="java.lang.String" name="name">
                <expression>node.labels[0] + node.getProperty('geneID')</expression>
            </column>
            <column type="java.lang.Long" name="refId">
                <id/>
            </column>
            <column type="java.lang.String" name="geneID">
                <property key="geneID" />
            </column>
            <column type="java.lang.Long" name="start">
                <property key="start" />
            </column>
            <column type="java.lang.Long" name="end">
                <property key="end" />
            </column>
            <column type="java.lang.String" name="nodeType">
                <label match=".*"/>
            </column>
        </node>
        <edge referenceIdColumn="refid">
            <column type="java.lang.Long" name="refid" >
                <id/>
            </column>
            <column type="java.lang.String" name="linkType">
                <edgeType/>
            </column>
        </edge>
    </mapping>
</cytemplate>
```
##XSD
```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<xs:schema version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema">

  <xs:element name="cytemplate" type="queryTemplate"/>

  <xs:complexType name="columnMapping">
    <xs:sequence>
      <xs:element name="node" type="nodeMapping" minOccurs="0"/>
      <xs:element name="edge" type="edgeMapping" minOccurs="0"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="nodeMapping">
    <xs:sequence>
      <xs:element name="column" type="nodeColumn" minOccurs="0" maxOccurs="unbounded"/>
    </xs:sequence>
    <xs:attribute name="referenceIdColumn" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="nodeColumn">
    <xs:sequence>
      <xs:element name="property" type="property" minOccurs="0"/>
      <xs:element name="id" type="id" minOccurs="0"/>
      <xs:element name="expression" type="expression" minOccurs="0"/>
      <xs:element name="label" type="label" minOccurs="0"/>
    </xs:sequence>
    <xs:attribute name="type" type="xs:string"/>
    <xs:attribute name="name" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="property">
    <xs:sequence/>
    <xs:attribute name="key" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="id">
    <xs:sequence/>
  </xs:complexType>

  <xs:simpleType name="expression">
    <xs:restriction base="xs:string"/>
  </xs:simpleType>

  <xs:complexType name="label">
    <xs:sequence/>
    <xs:attribute name="match" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="edgeMapping">
    <xs:sequence>
      <xs:element name="column" type="edgeColumn" minOccurs="0" maxOccurs="unbounded"/>
    </xs:sequence>
    <xs:attribute name="referenceIdColumn" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="edgeColumn">
    <xs:sequence>
      <xs:element name="property" type="property" minOccurs="0"/>
      <xs:element name="id" type="id" minOccurs="0"/>
      <xs:element name="edgetype" type="edgeType" minOccurs="0"/>
      <xs:element name="expression" type="expression" minOccurs="0"/>
    </xs:sequence>
    <xs:attribute name="type" type="xs:string"/>
    <xs:attribute name="name" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="edgeType">
    <xs:sequence/>
  </xs:complexType>

  <xs:complexType name="copyAll">
    <xs:sequence/>
    <xs:attribute name="referenceIdColumn" type="xs:string"/>
    <xs:attribute name="network" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="parameter">
    <xs:sequence/>
    <xs:attribute name="name" type="xs:string"/>
    <xs:attribute name="type" type="xs:string"/>
  </xs:complexType>

  <xs:complexType name="parameters">
    <xs:sequence>
      <xs:element name="parameter" type="parameter" minOccurs="0" maxOccurs="unbounded"/>
    </xs:sequence>
  </xs:complexType>

  <xs:complexType name="queryTemplate">
    <xs:sequence>
      <xs:element name="query" type="xs:string" minOccurs="0"/>
      <xs:element name="parameters" type="parameters" minOccurs="0"/>
      <xs:choice minOccurs="0">
        <xs:element name="mapping" type="columnMapping"/>
        <xs:element name="copyall" type="copyAll"/>
      </xs:choice>
    </xs:sequence>
    <xs:attribute name="name" type="xs:string"/>
  </xs:complexType>
</xs:schema>
```