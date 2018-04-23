package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.commands.CommandExecutor;
import org.cytoscape.work.Task;
import org.cytoscape.work.swing.DialogTaskManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandExecutorTest {

    @Mock
    private Services services;

    @Test
    public void create() throws Exception {
        CommandExecutor commandExecutor = CommandExecutor.create(services);
        assertNotNull("create command runner should not return null", commandExecutor);
    }

    @Test
    public void execute() throws Exception {
        Task task = mock(Task.class);
        DialogTaskManager dialogTaskManager = mock(DialogTaskManager.class);
        when(services.getDialogTaskManager()).thenReturn(dialogTaskManager);
        CommandExecutor commandExecutor = CommandExecutor.create(services);
        commandExecutor.execute(task);
        assertNotNull("create command runner should not return null", commandExecutor);
        verify(dialogTaskManager).execute(any());
    }

}