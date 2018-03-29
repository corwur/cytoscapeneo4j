package nl.corwur.cytoscape.neo4j.internal.commands.tasks.exportneo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.NetworkLabel;

public class ExportNetworkConfiguration {
    private final NetworkLabel networkLabel;
    

    public ExportNetworkConfiguration(NetworkLabel networkLabel) {
        this.networkLabel = networkLabel;
    }

    public NetworkLabel getNetworkLabel() {
        return networkLabel;
    }


    public static ExportNetworkConfiguration create(NetworkLabel networkLabel) {
        return new ExportNetworkConfiguration(networkLabel);
    }
}
