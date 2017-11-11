package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.xml;

import nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.CopyAllMappingStrategy;
import nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.CypherQueryTemplate;
import nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.mapping.GraphMapping;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderTest {

    @Test
    public void readCopyAllStrategy() throws Exception {
        Reader reader = new Reader();
        CypherQueryTemplate template = reader.read(getClass().getResourceAsStream("/gene-detail-copyall.xml"));
        assertNotNull(template);
        assertTrue(template.getMapping() instanceof CopyAllMappingStrategy);
        assertEquals("Copy All Network",((CopyAllMappingStrategy)template.getMapping()).getNetworkName());
        assertEquals("referenceId",((CopyAllMappingStrategy)template.getMapping()).getReferenceColumn());
    }

    @Test
    public void readMappingStrategy() throws Exception {
        Reader reader = new Reader();
        CypherQueryTemplate template = reader.read(getClass().getResourceAsStream("/gene-detail.xml"));
        assertNotNull(template);
        assertTrue(template.getMapping() instanceof GraphMapping);
        assertTrue(((GraphMapping)template.getMapping()).getNodeColumnMapping().size() >0 );
        assertTrue(((GraphMapping)template.getMapping()).getEdgeColumnMapping().size() >0 );
        assertNotNull(((GraphMapping)template.getMapping()).getNodeReferenceIdColumn());
        assertNotNull(((GraphMapping)template.getMapping()).getEdgeReferenceIdColumn());
    }

}