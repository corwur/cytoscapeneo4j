package nl.corwur.cytoscape.neo4j.internal.ui.connect;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.configuration.AppConfiguration;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import org.cytoscape.application.swing.CySwingApplication;

import javax.swing.*;

public class ConnectToNeo4j {

    private final Neo4jClient neo4jClient;
    private final CySwingApplication cySwingApplication;
    private final AppConfiguration appConfiguration;

    private ConnectToNeo4j(Neo4jClient neo4jClient, CySwingApplication cySwingApplication, AppConfiguration appConfiguration) {
        this.neo4jClient = neo4jClient;
        this.cySwingApplication = cySwingApplication;
        this.appConfiguration = appConfiguration;
    }

    public static ConnectToNeo4j create(Services services) {
        return new ConnectToNeo4j(
                services.getNeo4jClient(),
                services.getCySwingApplication(),
                services.getAppConfiguration());
    }


    public boolean openConnectDialogIfNotConnected() {
        if (neo4jClient.isConnected()) {
            return true;
        }
        return connect();
    }

    public boolean connect() {
        ConnectDialog connectDialog = new ConnectDialog
                (cySwingApplication.getJFrame(),
                        neo4jClient::connect,
                appConfiguration.getNeo4jProtocol(),
                appConfiguration.getNeo4jPort(),
                appConfiguration.getNeo4jHost(),
                appConfiguration.getNeo4jUsername(),
                appConfiguration.getNeo4jDatabase()
        );
        connectDialog.showConnectDialog();
        if (connectDialog.isOk()) {
            appConfiguration.setConnectionParameters(
                    connectDialog.getProtocol(),
                    connectDialog.getHostname(),
                    connectDialog.getUsername(),
                    connectDialog.getDatabase());
            appConfiguration.save();
            JOptionPane.showMessageDialog(this.cySwingApplication.getJFrame(), "Connected");
        }
        return neo4jClient.isConnected();
    }
}
