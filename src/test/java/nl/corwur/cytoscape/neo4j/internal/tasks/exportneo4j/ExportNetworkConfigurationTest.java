package nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.NodeLabel;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExportNetworkConfigurationTest {

    @Mock
    private CyNetwork cyNetwork;

    @Mock
    private CyNode cyNode;

    @Mock
    private CyEdge cyEdge;

    @Mock
    private CyRow cyRow;

    @Mock
    private CyRow cyEdgeRow;

    @Before
    public void before() {
        when(cyNetwork.getRow(cyNode)).thenReturn(cyRow);
        when(cyNetwork.getRow(cyEdge)).thenReturn(cyEdgeRow);
    }

    @Test
    public void create() {
        List<String> labels = Arrays.asList("label1", "label2");
        when(cyRow.isSet("label")).thenReturn(true);
        when(cyRow.getRaw("label")).thenReturn(labels);
        when(cyRow.get("label", List.class)).thenReturn(labels);

        when(cyRow.isSet("refId")).thenReturn(true);
        when(cyRow.getRaw("refId")).thenReturn(1l);
        when(cyRow.get("refId", Long.class)).thenReturn(1l);

        when(cyRow.get("shared name", String.class)).thenReturn("name");

        when(cyEdgeRow.isSet("shared name")).thenReturn(true);
        when(cyEdgeRow.getRaw("shared name")).thenReturn("link");
        when(cyEdgeRow.get("shared name", String.class)).thenReturn("link");

        ExportNetworkConfiguration exportNetworkConfiguration =
                ExportNetworkConfiguration.create(
                        NodeLabel.create("label"),
                        "shared name",
                        "refId",
                        "label",
                        "props"
                );
        assertEquals("label", exportNetworkConfiguration.getNodeLabel().getLabel());
        assertEquals("link", exportNetworkConfiguration.getRelationship(cyNetwork, cyEdge));
        assertEquals("refId", exportNetworkConfiguration.getNodeReferenceIdColumn());
        assertEquals("props", exportNetworkConfiguration.getNodePropertiesColumnName());
        assertEquals(2, exportNetworkConfiguration.getNodeLabels(cyNode, cyNetwork).size());
        assertEquals("label1", exportNetworkConfiguration.getNodeLabels(cyNode, cyNetwork).get(0).getLabel());
        assertEquals(1l, exportNetworkConfiguration.getNodeReferenceId(cyNode, cyNetwork));
        assertEquals("name", exportNetworkConfiguration.getNodeName(cyNetwork, cyNode));

    }

    @Test
    public void refId_SUID() {

        when(cyRow.isSet("refId")).thenReturn(false);
        when(cyNode.getSUID()).thenReturn(1l);
        ExportNetworkConfiguration exportNetworkConfiguration = ExportNetworkConfiguration.create(NodeLabel.create("label"), "relationship", "refId", "label", "props");
        assertEquals(1l, exportNetworkConfiguration.getNodeReferenceId(cyNode, cyNetwork));
        verify(cyNode).getSUID();
    }

    @Test
    public void relationshipDefault() {

        when(cyEdgeRow.isSet("relationship")).thenReturn(false);
        ExportNetworkConfiguration exportNetworkConfiguration =
                ExportNetworkConfiguration.create(
                        NodeLabel.create("label"), "relationship", "refId", "label", "props");
        assertEquals("relationship", exportNetworkConfiguration.getRelationship(cyNetwork, cyEdge));
    }

}