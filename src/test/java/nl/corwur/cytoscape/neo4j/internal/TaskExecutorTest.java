package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.tasks.TaskExecutor;
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
public class TaskExecutorTest {

    @Mock
    private Services services;

    @Test
    public void create() throws Exception {
        TaskExecutor taskExecutor = TaskExecutor.create(services);
        assertNotNull("create command runner should not return null", taskExecutor);
    }

    @Test
    public void execute() throws Exception {
        Task task = mock(Task.class);
        DialogTaskManager dialogTaskManager = mock(DialogTaskManager.class);
        when(services.getDialogTaskManager()).thenReturn(dialogTaskManager);
        TaskExecutor taskExecutor = TaskExecutor.create(services);
        taskExecutor.execute(task);
        assertNotNull("create command runner should not return null", taskExecutor);
        verify(dialogTaskManager).execute(any());
    }

}