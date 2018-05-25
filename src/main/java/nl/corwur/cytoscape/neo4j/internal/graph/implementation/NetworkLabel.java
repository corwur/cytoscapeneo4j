package nl.corwur.cytoscape.neo4j.internal.graph.implementation;

public class NetworkLabel extends Label {

    private NetworkLabel(String label) {
        super(label);
    }

    public static Label create(String label) {
        if (label.matches("[\\w\\d\\s]+")) {
            return new NetworkLabel(label);
        }
        throw new IllegalStateException();
    }

}
