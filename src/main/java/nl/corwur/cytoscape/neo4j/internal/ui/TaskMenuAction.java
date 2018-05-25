package nl.corwur.cytoscape.neo4j.internal.ui;

import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskExecutor;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectToNeo4j;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.AbstractTask;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;

public class TaskMenuAction extends AbstractCyAction {

    private static final String MENU_LOC = "Apps.Cypher Queries";
    private final transient ConnectToNeo4j connectToNeo4j;
    private final transient TaskExecutor taskExecutor;
    private final transient Supplier<AbstractTask> taskSupplier;

    public static TaskMenuAction create(String menuTitle, Services services, Supplier<AbstractTask> taskSupplier) {
        return new TaskMenuAction(menuTitle, services, taskSupplier);
    }

    private TaskMenuAction(String menuTitle, Services services, Supplier<AbstractTask> taskSupplier) {
        super(menuTitle);
        this.taskSupplier = taskSupplier;
        this.taskExecutor = services.getTaskExecutor();
        this.connectToNeo4j = ConnectToNeo4j.create(services);
        setPreferredMenu(MENU_LOC);
        setEnabled(false);
        setMenuGravity(0.1f);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (connectToNeo4j.openConnectDialogIfNotConnected()) {
            AbstractTask abstractTask = taskSupplier.get();
            taskExecutor.execute(abstractTask);
        }
    }
}
