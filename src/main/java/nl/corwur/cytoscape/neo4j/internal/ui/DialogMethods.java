package nl.corwur.cytoscape.neo4j.internal.ui;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectToNeo4j;

import javax.swing.*;
import java.awt.*;

public class DialogMethods {

    private DialogMethods() {
    }

    public static void centerAndShow(JDialog jDialog) {
        center(jDialog);
        jDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        jDialog.setModal(true);
        jDialog.setResizable(true);
        jDialog.pack();
        jDialog.setVisible(true);
    }

    public static void center(JDialog jDialog) {
        Point cyLocation = jDialog.getParent().getLocation();
        int height = jDialog.getParent().getHeight();
        int width = jDialog.getParent().getWidth();
        jDialog.setLocation(new Point(cyLocation.x + (width / 4), cyLocation.y + (height / 4)));
    }

    public static boolean connect(Services services) {
        ConnectToNeo4j connectToNeo4j = ConnectToNeo4j.create(services);
        if (!connectToNeo4j.openConnectDialogIfNotConnected()) {
            JOptionPane.showMessageDialog(services.getCySwingApplication().getJFrame(), "Not connected");
            return false;
        }
        return true;

    }
}
