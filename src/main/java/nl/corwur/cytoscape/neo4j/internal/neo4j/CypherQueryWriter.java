package nl.corwur.cytoscape.neo4j.internal.neo4j;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Writer;


public class CypherQueryWriter {

    private Writer writer;

    public CypherQueryWriter(Writer writer) {
        this.writer = writer;
    }

    public void write(CypherQuery cypherQuery) throws IOException {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(cypherQuery);
        writer.write(json);
    }
}
