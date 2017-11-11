package nl.corwur.cytoscape.neo4j.internal;

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
public class CommandRunnerTest {

    @Mock
    private Services services;

    @Test
    public void create() throws Exception {
        CommandRunner commandRunner = CommandRunner.create(services);
        assertNotNull("create command runner should not return null", commandRunner);
    }

    @Test
    public void execute() throws Exception {
        Task task = mock(Task.class);
        DialogTaskManager dialogTaskManager = mock(DialogTaskManager.class);
        when(services.getDialogTaskManager()).thenReturn(dialogTaskManager);
        CommandRunner commandRunner = CommandRunner.create(services);
        commandRunner.execute(task);
        assertNotNull("create command runner should not return null", commandRunner);
        verify(dialogTaskManager).execute(any());
    }

}