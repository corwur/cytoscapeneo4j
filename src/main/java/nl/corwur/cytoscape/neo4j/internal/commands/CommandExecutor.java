package nl.corwur.cytoscape.neo4j.internal.commands;

import nl.corwur.cytoscape.neo4j.internal.Services;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

/**
 * This class executes a command in the cytoscape dialog task manager
 */
public class CommandExecutor {

    private final DialogTaskManager dialogTaskManager;

    public static CommandExecutor create(Services services) {
        return new CommandExecutor(services.getDialogTaskManager());
    }

    private CommandExecutor(DialogTaskManager dialogTaskManager) {
        this.dialogTaskManager = dialogTaskManager;
    }

    public void execute(Task task) {
        TaskIterator it = new TaskIterator(task);
        dialogTaskManager.execute(it);
    }
}
