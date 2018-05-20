package nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.*;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.NodeLabel;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.PropertyKey;
import org.cytoscape.model.*;

import java.util.*;
import java.util.stream.Collectors;

public class ExportNew {

    private static final String SUID = "SUID";
    private static final String NEO4JLABELS = "_neo4jlabels";
    private final CyNetwork cyNetwork;
    private final CommandBuilder commandBuilder = new CommandBuilder();

    public static ExportNew create(CyNetwork cyNetwork, GraphImplementation graphImplementation) {
        return new ExportNew(cyNetwork, graphImplementation);
    }

    private ExportNew(CyNetwork cyNetwork, GraphImplementation graphImplementation) {
        this.cyNetwork = cyNetwork;
        this.commandBuilder.graphImplementation(graphImplementation);
    }

    public Command compute() {
        for(CyNode cyNode : cyNetwork.getNodeList()) {
            commandBuilder.addNode(getNodeLabels(cyNode), getProperties(cyNode));
        }
        for(CyEdge cyEdge : cyNetwork.getEdgeList()) {
            commandBuilder.addEdge(suid(cyEdge.getSource()), suid(cyEdge.getTarget()), getProperties(cyEdge));
        }
        return commandBuilder.build();
    }


    private PropertyKey<Long> suid(CyIdentifiable cyIdentifiable) {
        return new PropertyKey<>(SUID, cyIdentifiable.getSUID());
    }

    private Map<String,Object> getProperties(CyIdentifiable cyIdentifiable) {
        CyRow cyRow = cyNetwork.getRow(cyIdentifiable);
        Map<String, Object> properties = new HashMap<>(cyRow.getAllValues());
        properties.put(SUID, cyIdentifiable.getSUID());
        return properties;
    }

    private List<NodeLabel> getNodeLabels(CyIdentifiable cyIdentifiable) {
        CyRow cyRow = cyNetwork.getRow(cyIdentifiable);
        if(cyRow.isSet(NEO4JLABELS)) {
            return cyRow.getList(NEO4JLABELS, String.class).stream()
                    .map(NodeLabel::create)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
