package nl.corwur.cytoscape.neo4j.internal.neo4j;

public class ConnectionParameter {
    private final String host;
    private final String username;
    private final char[] password;
    private final String database;

    public ConnectionParameter(String url, String username, char[] password, String database) {
        this.host = url;
        this.username = username;
        this.password = password;
        this.database = database;
    }

    String getBoltUrl() {
        return "neo4j://" + host;
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
