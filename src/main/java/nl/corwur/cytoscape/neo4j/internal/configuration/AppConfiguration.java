package nl.corwur.cytoscape.neo4j.internal.configuration;

import nl.corwur.cytoscape.neo4j.internal.neo4j.ConnectionParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Properties;

/**
 * This clas stores data that is used by the plugin.
 * The settings are stored in a property file the temporary directory (java.io.tmpdir).
 */
public class AppConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfiguration.class);

    private static final int BOLT_PORT = 7687;
    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    private static final String TEMPLATEDIR = "templatedir";
    private static final String NEO4J_PROTOCOL = "neo4j.protocol";
    private static final String NEO4J_HOST = "neo4j.host";
    private static final String NEO4J_PORT = "neo4j.port";
    private static final String NEO4J_USERNAME = "neo4j.username";
    private static final String NEO4J_DATABASE = "neo4j.database";
    private Properties properties = new Properties();

    public String getTemplateDirectory() {
        return properties.getProperty(TEMPLATEDIR);
    }

    public String getNeo4jHost() {
        return properties.getProperty(NEO4J_HOST);
    }

    public ConnectionParameter.Protocol getNeo4jProtocol() {
        try {
            return ConnectionParameter.Protocol.valueOf(properties.getProperty(NEO4J_PROTOCOL));
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Invalid protocol using bolt:// {}", e);
            return ConnectionParameter.Protocol.BOLT;
        }
    }

    public int getNeo4jPort() {
        try {
            return Integer.parseInt(properties.getProperty(NEO4J_PORT));
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid port number using default bolt port (7687) {}", e);
            return BOLT_PORT;
        }
    }


    public String getNeo4jUsername() {
        return properties.getProperty(NEO4J_USERNAME);
    }

    public String getNeo4jDatabase() { return properties.getProperty(NEO4J_DATABASE); }

    public void setTemplateDirectory(String templateDir) {
        properties.setProperty(TEMPLATEDIR, templateDir);
        save();
    }

    public void load() {
        Path configurationPath = getConfigurationFile();
        if (configurationPath.toFile().exists()) {
            try {
                properties.load(Files.newInputStream(configurationPath, StandardOpenOption.READ));
            } catch (IOException e) {
                LOG.warn("Error reading configuration");
            }
        } else {
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty(NEO4J_PROTOCOL, "bolt");
        properties.setProperty(NEO4J_PORT, "7687");
        properties.setProperty(NEO4J_HOST, "localhost");
        properties.setProperty(NEO4J_USERNAME, "neo4j");
        properties.setProperty(NEO4J_DATABASE, "neo4j");
        properties.setProperty(TEMPLATEDIR, "");
    }

    public void save() {
        Path configurationFile = getConfigurationFile();
        try {
            properties.store(Files.newOutputStream(configurationFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING), "saved app config: " + LocalDateTime.now());
        } catch (IOException e) {
            LOG.warn("Error writing config file");
        }
    }

    private Path getConfigurationFile() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        return Paths.get(tmpDir, "corwur-cyneo4j.properties");
    }

    public void setConnectionParameters(String protocol,String hostname, String username, String database) {
        properties.setProperty(NEO4J_PROTOCOL, protocol);
        properties.setProperty(NEO4J_HOST, hostname);
        properties.setProperty(NEO4J_USERNAME, username);
        properties.setProperty(NEO4J_DATABASE, database);
    }


}
