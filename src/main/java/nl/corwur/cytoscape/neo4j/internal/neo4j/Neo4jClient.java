package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddEdgeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.AddNodeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObject;
import nl.corwur.cytoscape.neo4j.internal.graph.commands.NodeLabel;
import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.AuthenticationException;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;

import java.util.List;

public class Neo4jClient {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Neo4jClient.class);

    private Driver driver;
    private Neo4jGraphFactory neo4JGraphFactory = new Neo4jGraphFactory();

    public boolean connect(ConnectionParameter connectionParameter) {
        try {
            driver = GraphDatabase.driver(
                connectionParameter.getBoltUrl(),
                AuthTokens.basic(
                    connectionParameter.getUsername(),
                    connectionParameter.getPasswordAsString()
                ),
                Config.build().withoutEncryption().toConfig()
            );
            return true;
        } catch (AuthenticationException | ServiceUnavailableException e) {
            logger.warn("Cannot connect to Neo4j", e);
            return false;
        }
    }
    public List<GraphObject> executeQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = driver.session()) {
            StatementResult statementResult = session.run(cypherQuery.getQuery(), cypherQuery.getParams());
            return statementResult.list( neo4JGraphFactory::create);
        } catch (Exception e) {
            throw new Neo4jClientException();
        }
    }

    public void explainQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = driver.session()) {
            session.run(cypherQuery.getExplainQuery(), cypherQuery.getParams());
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public boolean isConnected() {
        return driver != null && driver.session().isOpen();
    }

    public void executeCommand(AddEdgeCommand cmd) throws Neo4jClientException {
        CypherQuery cypherQuery = CypherQuery.builder()
                .query("MATCH (s:" + cmd.getNodeLabel().getLabel() + " {"+cmd.getStartNodeIdProperty()+":$"+cmd.getStartNodeIdParameter()+"}), (e:" + cmd.getNodeLabel().getLabel() + " {"+cmd.getEndNodeIdProperty()+":$"+cmd.getEndNodeIdParameter()+"}) CREATE (s) -[:" + cmd.getRelationship() + " $" + cmd.getEdgePropertiesName() + "]-> (e)")
                .params(cmd.getStartNodeIdParameter(), cmd.getStartId())
                .params(cmd.getEndNodeIdParameter(), cmd.getEndId())
                .params(cmd.getRelationshipName(), cmd.getRelationship())
                .params(cmd.getEdgePropertiesName(), cmd.getEdgeProperties())
                .build();
        executeQuery(cypherQuery);
    }

    public void executeCommand(AddNodeCommand cmd) throws Neo4jClientException {
    	NodeLabel nodeName = cmd.getNodeLabel();
    	String nodeIdMatchClause = cmd.getNodeIdPropertyName()+ ":$" + cmd.getNodeIdPropertyName();
        String nodeIdSetClause = "n." + cmd.getNodeIdPropertyName()+ "=$" + cmd.getNodeIdPropertyName();
        String nodeNameSetClause = "n.name ='" + cmd.getNodeName() + "'";
        String nodeLabelClause = cmd.getNodeLabelList().stream().reduce( "", (str, label) -> str + ":" + label.getLabel(), (s1, s2) -> s1 + s2);

        CypherQuery removerRelationsQuery = CypherQuery.builder().query("MATCH(n:" + nodeName.getLabel() + " {" + nodeIdMatchClause + "}) - [r] - (e) DELETE r")
                .params(cmd.getNodeIdPropertyName(), cmd.getNodeId())
                .build();
        executeQuery(removerRelationsQuery);

        CypherQuery removerQuery = CypherQuery.builder().query("MATCH(n:" + nodeName.getLabel() + " {" + nodeIdMatchClause + "}) DELETE n")
                .params(cmd.getNodeIdPropertyName(), cmd.getNodeId())
                .build();
        executeQuery(removerQuery);
        CypherQuery insertQuery = CypherQuery.builder().query(
                "CREATE(n:" + nodeName.getLabel() + " $" +cmd.getNodePropertiesName()+ ") " +
                        "SET " + nodeNameSetClause + ", " + nodeIdSetClause + (nodeLabelClause.isEmpty() ? "" : ", n" + nodeLabelClause))
                .params(cmd.getNodePropertiesName(),cmd.getNodeProperties())
                .params(cmd.getNodeIdPropertyName(), cmd.getNodeId())
                .build();
        executeQuery(insertQuery);
    }
}
