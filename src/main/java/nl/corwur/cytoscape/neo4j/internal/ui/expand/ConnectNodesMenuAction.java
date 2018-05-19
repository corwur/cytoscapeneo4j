/**
 * 
 */
package nl.corwur.cytoscape.neo4j.internal.ui.expand;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.NetworkTaskFactory;

import org.cytoscape.work.TaskIterator;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.commands.tasks.expand.ConnectNodesTask;

/**
 * @author sven
 *
 */
public class ConnectNodesMenuAction implements NetworkTaskFactory {
	
	private static final long serialVersionUID = 1L;
    private final transient Services services;
	private Boolean onlySelected;
	
    /**
	 * 
	 */
	public ConnectNodesMenuAction(Services services, Boolean onlySelected) {
		this.services = services;
		this.onlySelected = onlySelected;
	}

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		if (this.isReady(network)) {
			return new TaskIterator(new ConnectNodesTask(services, network, this.onlySelected));
		}
		else {
			return null;
		}
	}

	@Override
	public boolean isReady(CyNetwork arg0) {
		return arg0.getNodeCount() > 0;
	}

	public static ConnectNodesMenuAction create(Services services, Boolean onlySelected) {
		return new ConnectNodesMenuAction(services, onlySelected);
	}



}
