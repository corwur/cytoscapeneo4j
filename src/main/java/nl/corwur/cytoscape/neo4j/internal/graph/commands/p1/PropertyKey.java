package nl.corwur.cytoscape.neo4j.internal.graph.commands.p1;

public class PropertyKey<T> {
    private final String name;
    private final T value;

    public PropertyKey(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }
}
