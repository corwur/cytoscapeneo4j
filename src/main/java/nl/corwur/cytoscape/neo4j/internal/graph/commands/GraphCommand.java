package nl.corwur.cytoscape.neo4j.internal.graph.commands;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.p1.GraphImplementation;

public abstract class GraphCommand extends Command {
    protected GraphImplementation graphImplementation;

    public GraphImplementation getGraphImplementation() {
        return graphImplementation;
    }

    public void setGraphImplementation(GraphImplementation graphImplementation) {
        this.graphImplementation = graphImplementation;
    }
}
