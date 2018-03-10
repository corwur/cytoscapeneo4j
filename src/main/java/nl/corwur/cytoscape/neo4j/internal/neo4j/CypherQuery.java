package nl.corwur.cytoscape.neo4j.internal.neo4j;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a Neo4j Cypher query.
 */
public class CypherQuery {

    private final String query;
    private final Map<String, Object> params;

    CypherQuery(String query, Map<String, Object> params) {
        this.query = query;
        this.params = params;
    }

    Map<String, Object> getParams() {
        return params;
    }

    String getQuery() {
        return query;
    }

    String getExplainQuery() {
        return "EXPLAIN " + query;
    }

    public static final class Builder {

        private String query;
        private Map<String, Object> params;

        private Builder() {
            params = new HashMap<>();
        }

        public Builder query(String query) {
            this.query = query;
            return this;
        }

        public Builder params(String param, Object value) {
            this.params.put(param, value);
            return this;
        }

        public Builder params(Map<String,Object> map) {
            this.params.putAll(map);
            return this;
        }



        public CypherQuery build() {
            return new CypherQuery(query, params);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
