package nl.corwur.cytoscape.neo4j.internal.ui.importgraph.expand;

import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTask;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class ExpandNodeTask extends AbstractNodeViewTask implements Task {

	public ExpandNodeTask(View<CyNode> nodeView, CyNetworkView netView) {
		super(nodeView, netView);
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
