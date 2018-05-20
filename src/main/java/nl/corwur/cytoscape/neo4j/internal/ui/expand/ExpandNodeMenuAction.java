/**
 * 
 */
package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.TaskIterator;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask;


/**
 * @author sven
 *
 */
public class ExpandNodeMenuAction extends AbstractNodeViewTaskFactory {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private final transient Services services;
	private Boolean redoLayout;
    
    public static ExpandNodeMenuAction create(Services services, Boolean redoLayout ) {
        return new ExpandNodeMenuAction(services, redoLayout);
    }

    private ExpandNodeMenuAction(Services services, Boolean redoLayout) {
        this.services = services;
        this.redoLayout = redoLayout;
    }
       
	@Override
	public TaskIterator createTaskIterator(View<CyNode> nodeView, CyNetworkView networkView) {
		if (this.isReady(nodeView, networkView)) {
			return new TaskIterator(new ExpandNodeTask(nodeView, networkView, this.services, this.redoLayout, null));
		}
		else {
			return null;
		}
	}
	

}
