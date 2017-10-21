package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate;

import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;

import java.util.HashMap;
import java.util.Map;

public class CypherQueryTemplate {

    private String name;
    private String cypherQuery;
    private Map<String,Class<?>> parameterTypes;
    private Map<String,Object> parameters;
    private MappingStrategy mapping;

    CypherQueryTemplate(
            String name,
            String query,
            Map<String, Class<?>> parameterTypes,
            MappingStrategy mapping) {
        this.name = name;
        this.cypherQuery = query;
        this.parameterTypes = new HashMap<>(parameterTypes);
        this.mapping = mapping;
        this.parameters = new HashMap<>();
        for(String key : parameterTypes.keySet()) {
            parameters.put(key, null);
        }
    }

    public CypherQuery createQuery() {
        return CypherQuery.builder()
                .query(cypherQuery)
                .params(parameters)
                .build();
    }

    public MappingStrategy getMapping() {
        return mapping;
    }

    public String getName() {
        return name;
    }

    public String getCypherQuery() {
        return cypherQuery;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameterMap) {
        for(Map.Entry<String,Object> entry : parameterMap.entrySet()) {
            if(this.parameters.containsKey(entry.getKey())) { //TODO: check type
                this.parameters.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static class Builder {

        private String query;
        private String name;
        private Map<String, Class<?>> parameterTypes = new HashMap<>();
        private MappingStrategy mappingStrategy;

        public Builder setName(String name) {
            this.name =name;
            return this;
        }

        public Builder setQueryTemplate(String query) {
            this.query =query;
            return this;
        }

        public Builder addParameter(String parameter, Class<?> type) {
            parameterTypes.put(parameter, type);
            return this;
        }

        public Builder addMappingStrategy(MappingStrategy mappingStrategy) {
            this.mappingStrategy = mappingStrategy;
            return this;
        }

        public CypherQueryTemplate build() {
            CypherQueryTemplate cypherQueryTemplate = new CypherQueryTemplate(
                    name,
                    query,
                    parameterTypes,
                    mappingStrategy
            );
            return cypherQueryTemplate;
        }
    }
}
