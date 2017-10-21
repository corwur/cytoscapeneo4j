package nl.corwur.cytoscape.neo4j.internal;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;

public class CommandRunner {

    private final DialogTaskManager dialogTaskManager;

    static CommandRunner create(Services services) {
        return new CommandRunner(services.getDialogTaskManager());
    }

    private CommandRunner(DialogTaskManager dialogTaskManager) {
        this.dialogTaskManager = dialogTaskManager;
    }

    public void execute(Task task) {
        TaskIterator it = new TaskIterator(task);
        dialogTaskManager.execute(it);
    }
}
