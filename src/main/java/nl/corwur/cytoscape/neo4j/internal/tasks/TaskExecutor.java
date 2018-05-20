package nl.corwur.cytoscape.neo4j.internal.tasks;

import nl.corwur.cytoscape.neo4j.internal.Services;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * This class executes a command in the cytoscape dialog task manager
 */
public class TaskExecutor {

    private final DialogTaskManager dialogTaskManager;

    public static TaskExecutor create(Services services) {
        return new TaskExecutor(services.getDialogTaskManager());
    }

    private TaskExecutor(DialogTaskManager dialogTaskManager) {
        this.dialogTaskManager = dialogTaskManager;
    }

    public void execute(Task task) {
        TaskIterator it = new TaskIterator(task);
        dialogTaskManager.execute(it);
    }
}
