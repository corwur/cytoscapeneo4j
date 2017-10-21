package nl.corwur.cytoscape.neo4j.internal.cypher.querytemplate.mapping.values;

import nl.corwur.cytoscape.neo4j.internal.graph.GraphNode;

import java.util.regex.Pattern;

public class LabelValue implements ValueExpression<GraphNode, String> {

    Pattern pattern;

    public LabelValue(String matches) {
        pattern = Pattern.compile(matches);
    }

    @Override
    public String eval(GraphNode val) {
        return val.getLabels().stream()
                .filter(pattern.asPredicate())
                .findFirst()
                .orElse("");
    }

    @Override
    public void accept(ValueExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Pattern getPattern() {
        return pattern;
    }
}
