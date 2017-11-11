package nl.corwur.cytoscape.neo4j.internal.ui.connect;

import nl.corwur.cytoscape.neo4j.internal.Services;
import org.cytoscape.application.swing.AbstractCyAction;

import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class ConnectInstanceMenuAction extends AbstractCyAction {

    private static final String MENU_TITLE = "Connect to Neo4j Instance";
    private static final String MENU_LOC = "Apps.Cypher Queries";
    private final transient ConnectToNeo4j connectToNeo4j;

    public static ConnectInstanceMenuAction create(Services services) {
        return new ConnectInstanceMenuAction(ConnectToNeo4j.create(services));
    }

    private ConnectInstanceMenuAction(ConnectToNeo4j connectToNeo4j) {
        super(MENU_TITLE);
        this.connectToNeo4j = connectToNeo4j;
        setPreferredMenu(MENU_LOC);
        setMenuGravity(0.0f);
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        connectToNeo4j.connect();
    }

}
