package nl.corwur.cytoscape.neo4j.internal.graph.commands.p1;

public class NetworkLabel extends Label {

	public NetworkLabel(String label) {
		super(label);

	}

	public static Label create(String label) {
        if(label.matches("[\\w\\d\\s]+")) {
            return new NetworkLabel(label);
        }
        throw new IllegalStateException();
     }

}
