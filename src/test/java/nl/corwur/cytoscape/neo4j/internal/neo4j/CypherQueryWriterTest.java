package nl.corwur.cytoscape.neo4j.internal.neo4j;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class CypherQueryWriterTest {

    @Test
    public void write() throws IOException {
        StringWriter stringWriter = new StringWriter();
        CypherQueryWriter writer = new CypherQueryWriter(stringWriter);
        writer.write(
                CypherQuery.builder().query("cypherquery")
                    .params("param1",1)
                    .params("param2", 1l)
                    .params("param3", "string")
                    .params("param4", 1.0)
                    .params("param5", 2.0f)
                .build()
        );
        CypherQuery query= new GsonBuilder().create().fromJson(stringWriter.toString(), CypherQuery.class);
        assertEquals("cypherquery", query.getQuery());
        //assertEquals(1, query.getParams().get("param1"));
        //assertEquals(1l, query.getParams().get("param1"));
        assertEquals("string", query.getParams().get("param3"));
        assertEquals(1.0, query.getParams().get("param4"));
//        assertEquals(2.0f,query.getParams().get("param5"));
    }
}