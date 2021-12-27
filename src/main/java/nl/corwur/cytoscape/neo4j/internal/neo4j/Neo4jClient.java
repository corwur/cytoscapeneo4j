package nl.corwur.cytoscape.neo4j.internal.neo4j;

import nl.corwur.cytoscape.neo4j.internal.graph.Graph;

import java.util.List;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.exceptions.AuthenticationException;
import org.neo4j.driver.exceptions.ServiceUnavailableException;

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
                    )
            );
            driver.verifyConnectivity();
            return true;
        } catch (AuthenticationException | ServiceUnavailableException e) {
            logger.warn("Cannot connect to Neo4j", e);
            return false;
        }
    }

    public void executeQuery(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = driver.session()) {
            session.run(cypherQuery.getQuery(), cypherQuery.getParams());
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public Graph getGraph(CypherQuery cypherQuery) throws Neo4jClientException {
        try (Session session = driver.session()) {
            Result statementResult = session.run(cypherQuery.getQuery(), cypherQuery.getParams());
            return Graph.createFrom(statementResult.list(neo4JGraphFactory::create));
        } catch (Exception e) {
            throw new Neo4jClientException(e.getMessage(), e);
        }
    }

    public List<Record> getResults(CypherQuery cypherQuery) throws Neo4jClientException {
    	if (driver == null) {
            throw new Neo4jClientException("No Neo4j database connection or driver available.");
    	}
        try (Session session = driver.session()) {
            Result statementResult = session.run(cypherQuery.getQuery(), cypherQuery.getParams());
            return statementResult.list();
        } catch (Exception e) {
            throw new Neo4jClientException("No Neo4j session available or error getting results.");
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

    public void close() {
        if (isConnected()) {
            driver.close();
        }
    }
}
