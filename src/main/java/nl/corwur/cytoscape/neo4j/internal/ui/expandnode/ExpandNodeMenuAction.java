/**
 * 
 */
package nl.corwur.cytoscape.neo4j.internal.ui.expandnode;

import java.awt.event.ActionEvent;

import org.cytoscape.application.swing.AbstractCyAction;

import nl.corwur.cytoscape.neo4j.internal.Services;


/**
 * @author sven
 *
 */
public class ExpandNodeMenuAction extends AbstractCyAction {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String MENU_TITLE = "Expand node";
    private static final String MENU_LOC = "Apps.Cypher Queries";
    private final transient Services services;
    
    public static ExpandNodeMenuAction create(Services services) {
        return new ExpandNodeMenuAction(services);
    }

    private ExpandNodeMenuAction(Services services) {
        super(MENU_TITLE, services.getCyApplicationManager(), "selectedNetworkObjs", services.getCyNetworkViewManager());
        this.services = services;
       
        setPreferredMenu(MENU_LOC);
        setEnabled(true);
        setMenuGravity(0.5f);

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {

    
    }

}
