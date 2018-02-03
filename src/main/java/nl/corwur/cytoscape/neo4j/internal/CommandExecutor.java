package nl.corwur.cytoscape.neo4j.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class CommandExecutor {

    private final DialogTaskManager dialogTaskManager;

    static CommandExecutor create(Services services) {
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
