package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.AddEdgeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.AddNodeCommand;
import nl.corwur.cytoscape.neo4j.internal.graph.GraphObject;
import nl.corwur.cytoscape.neo4j.internal.graph.NodeLabel;
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
                .query("MATCH (s:" + cmd.getNodeLabel().getLabel() + " {suid:$startNodeId}), (e:" + cmd.getNodeLabel().getLabel() + " {suid:$endNodeId}) CREATE (s) -[:LINK]-> (e)")
                .params("startNodeId", cmd.getStartId())
                .params("endNodeId", cmd.getEndId())
                .params("relationship", cmd.getRelationship())
                .params(cmd.getEdgeProperties())
                .build();
        executeQuery(cypherQuery);
    }

    public void executeCommand(AddNodeCommand cmd) throws Neo4jClientException {
    	NodeLabel nodeName = cmd.getNodeLabel();
        CypherQuery removerQuery = CypherQuery.builder().query("MATCH(n {suid:$suid}) DELETE n")
                .params("suid", cmd.getNodeId())
                .build();
        executeQuery(removerQuery);
        CypherQuery insertQuery = CypherQuery.builder().query("CREATE(n:" + nodeName.getLabel() + " $props) SET n.suid=$suid")
                .params("props",cmd.getNodeProperties())
                .params("suid", cmd.getNodeId())
                .build();
        executeQuery(insertQuery);
    }
}
