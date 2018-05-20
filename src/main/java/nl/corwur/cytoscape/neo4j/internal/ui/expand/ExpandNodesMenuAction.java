/**
 * 
 */
package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodesTask;

/**
 * @author sven
 *
 */
public class ExpandNodesMenuAction implements NetworkTaskFactory {
	
	private static final long serialVersionUID = 1L;
    private final transient Services services;
	private Boolean onlySelected;
	
    /**
	 * 
	 */
	public ExpandNodesMenuAction(Services services, Boolean onlySelected) {
		this.services = services;
		this.onlySelected = onlySelected;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		if (this.isReady(network)) {
			return new TaskIterator(new ExpandNodesTask(services, network, this.onlySelected));
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		return arg0 != null && arg0.getNodeCount() > 0;
	}

	public static ExpandNodesMenuAction create(Services services, Boolean onlySelected) {
		return new ExpandNodesMenuAction(services, onlySelected);
	}

}
