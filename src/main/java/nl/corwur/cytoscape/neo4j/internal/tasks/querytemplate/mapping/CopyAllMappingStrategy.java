package nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.mapping;

import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.DefaultImportStrategy;
import nl.corwur.cytoscape.neo4j.internal.tasks.importgraph.ImportGraphStrategy;

/**
 * This class implements the copy all mapping strategy.
 */
public class CopyAllMappingStrategy implements MappingStrategy {

    private final String referenceColumn;
    private final String networkName;

    public CopyAllMappingStrategy(String referenceColumn, String networkName) {
        this.referenceColumn = referenceColumn;
        this.networkName = networkName;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public String getNetworkName() {
        return networkName;
    }

    @Override
    public void accept(MappingStrategyVisitor visitor) {
        visitor.visit(this);
    }
}
