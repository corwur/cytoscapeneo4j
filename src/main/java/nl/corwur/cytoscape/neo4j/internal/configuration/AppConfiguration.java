package nl.corwur.cytoscape.neo4j.internal.configuration;

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

    private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);
    private static final String TEMPLATEDIR = "templatedir";
    private static final String NEO4J_HOST = "neo4j.host";
    private static final String NEO4J_USERNAME = "neo4j.username";
    private Properties properties = new Properties();

    public String getTemplateDirectory() {
        return properties.getProperty(TEMPLATEDIR);
    }

    public String getNeo4jHost() {
        return properties.getProperty(NEO4J_HOST);
    }

    public String getNeo4jUsername() {
        return properties.getProperty(NEO4J_USERNAME);
    }

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
        properties.setProperty(NEO4J_HOST, "localhost");
        properties.setProperty(NEO4J_USERNAME, "neo4j");
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

    public void setConnectionParameters(String hostname, String username) {
        properties.setProperty(NEO4J_HOST, hostname);
        properties.setProperty(NEO4J_USERNAME, username);
    }
}
