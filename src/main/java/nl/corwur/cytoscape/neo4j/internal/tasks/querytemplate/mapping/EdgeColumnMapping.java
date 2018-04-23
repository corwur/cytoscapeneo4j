package nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping;

import nl.corwur.cytoscape.neo4j.internal.commands.tasks.querytemplate.mapping.values.ValueExpression;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphEdge;

/**
 * This class implements a mapping from a value expression to a Cytoscape column.
 * @param <T>
 */
public class EdgeColumnMapping<T> {
    private final String columnName;
    private final Class<T> columnType;
    private final ValueExpression<GraphEdge,T> valueExpression;

    public EdgeColumnMapping(String columnName, Class<T> columnType, ValueExpression<GraphEdge, T> valueExpression) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.valueExpression = valueExpression;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<T> getColumnType() {
        return columnType;
    }

    public ValueExpression<GraphEdge, T> getValueExpression() {
        return valueExpression;
    }
}
