package nl.corwur.cytoscape.neo4j.internal.neo4j;

import com.google.gson.GsonBuilder;

import java.io.Reader;

public class CypherQueryReader {

    private final Reader reader;

    public CypherQueryReader(Reader reader) {
        this.reader = reader;
    }

    public CypherQuery read() {
        return new GsonBuilder().create().fromJson(reader, CypherQuery.class);
    }
}
