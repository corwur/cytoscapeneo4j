package nl.corwur.cytoscape.neo4j.internal.neo4j;

public class ConnectionParameter {

    public enum Protocol {
        BOLT("bolt"),
        NEO4J("neo4j");


        private final String protocol;
        Protocol(String protocol) {
            this.protocol = protocol;
        }

    }

    private final int port;
    private final Protocol protocol;
    private final String hostname;
    private final String username;
    private final char[] password;
    private final String database;

    public ConnectionParameter(Protocol protocol, String hostname, int port, String username, char[] password, String database) {
        this.protocol = protocol;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }

    String getUrl() {
        return protocol.protocol + "://" + hostname + ":" + port;
    }

    Protocol getProtocol() {
        return protocol;
    }

    String getUsername() {
        return username;
    }

    String getPasswordAsString() {
        return new String(password);
    }

    public String getDatabase() {
        return database;
    }
}
