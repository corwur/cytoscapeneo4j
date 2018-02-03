package nl.corwur.cytoscape.neo4j.internal.ui;

import nl.corwur.cytoscape.neo4j.internal.CommandExecutor;
import nl.corwur.cytoscape.neo4j.internal.Services;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectToNeo4j;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.work.AbstractTask;

import java.awt.event.ActionEvent;
import java.util.function.Supplier;

public class CommandMenuAction extends AbstractCyAction {

    private static final String MENU_LOC = "Apps.Cypher Queries";
    private final transient ConnectToNeo4j connectToNeo4j;
    private final transient CommandExecutor commandExecutor;
    private final transient Supplier<AbstractTask> taskSupplier;

    public static CommandMenuAction create(String menuTitle, Services services, Supplier<AbstractTask> taskSupplier) {
        return new CommandMenuAction(menuTitle, services, taskSupplier);
    }

    private CommandMenuAction(String menuTitle, Services services, Supplier<AbstractTask> taskSupplier) {
        super(menuTitle);
        this.taskSupplier = taskSupplier;
        this.commandExecutor = services.getCommandExecutor();
        this.connectToNeo4j = ConnectToNeo4j.create(services);
        setPreferredMenu(MENU_LOC);
        setEnabled(false);
        setMenuGravity(0.1f);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(connectToNeo4j.connect()) {
            AbstractTask abstractTask = taskSupplier.get();
            commandExecutor.execute(abstractTask);
        }
    }
}
