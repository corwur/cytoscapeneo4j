package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;

import java.util.List;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.AuthenticationException;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

public class Neo4jClient {
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Neo4jClient.class);

    private Driver driver;
    private Neo4jGraphFactory neo4JGraphFactory = new Neo4jGraphFactory();
    private String database;

    public boolean connect(ConnectionParameter connectionParameter) {
        try {
            driver = GraphDatabase.driver(
                    connectionParameter.getBoltUrl(),
                    AuthTokens.basic(
                            connectionParameter.getUsername(),
                            connectionParameter.getPasswordAsString()
                    )
            );
            database = connectionParameter.getDatabase();
            driver.verifyConnectivity();
            return true;
        } catch (AuthenticationException | ServiceUnavailableException e) {
            logger.warn("Cannot connect to Neo4j", e);
            return false;
        }
    }

    private Session getSession(){
        return driver.session(SessionConfig.builder().withDatabase(database).build());
    }

    public void executeQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = getSession()) {
            session.run(cypherQuery.getQuery(), cypherQuery.getParams());
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public Graph getGraph(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = getSession()) {
            Result statementResult = session.run(cypherQuery.getQuery(), cypherQuery.getParams());
            return Graph.createFrom(statementResult.list(neo4JGraphFactory::create));
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public List<Record> getResults(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = getSession()) {
            Result statementResult = session.run(cypherQuery.getQuery(), cypherQuery.getParams());
            return statementResult.list();
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }


    public void explainQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = getSession()) {
            session.run(cypherQuery.getExplainQuery(), cypherQuery.getParams());
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public boolean isConnected() {
        return driver != null && getSession().isOpen();
    }

    public void close() {
        if (isConnected()) {
            driver.close();
        }
    }
}
