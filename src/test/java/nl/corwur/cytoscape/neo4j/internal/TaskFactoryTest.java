package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.neo4j.CypherQuery;
import nl.corwur.cytoscape.neo4j.internal.tasks.AbstractImportTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExportNetworkToNeo4jTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.ImportQueryTemplateTask;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskFactory;
import nl.corwur.cytoscape.neo4j.internal.tasks.exportneo4j.ExportNetworkConfiguration;
import nl.corwur.cytoscape.neo4j.internal.tasks.querytemplate.CypherQueryTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class TaskFactoryTest {

    @Mock
    private Services services;

    @Test
    public void create() throws Exception {
        TaskFactory taskFactory = TaskFactory.create(services);
        assertNotNull("create command factory should not return null", taskFactory);
    }

    @Test
    public void createImportGraphTask() throws Exception {
        TaskFactory taskFactory = TaskFactory.create(services);
        AbstractImportTask task = taskFactory.createImportAllNodesAndEdgesFromNeo4jTask("Network", "default");
        assertNotNull("create import graph should not return null",task);
    }

    @Test
    public void createExportNetworkToNeo4jTask() throws Exception {
        TaskFactory taskFactory = TaskFactory.create(services);
        ExportNetworkConfiguration exportNetworkConfiguration = mock(ExportNetworkConfiguration.class);
        ExportNetworkToNeo4jTask task = taskFactory.createExportNetworkToNeo4jTask(exportNetworkConfiguration);
        assertNotNull("create export network to Neo4j should not return null",task);
    }

    @Test
    public void createRetrieveDataFromQueryTemplateTask() throws Exception {
        TaskFactory taskFactory = TaskFactory.create(services);
        CypherQueryTemplate query = mock(CypherQueryTemplate.class);
        ImportQueryTemplateTask task = taskFactory.createImportQueryTemplateTask("Networkname", query, "visualStyle");
        assertNotNull("create retrieve data from query-template should not return null",task);
    }

    @Test
    public void createExecuteCypherQueryTask() throws Exception {
        TaskFactory taskFactory = TaskFactory.create(services);
        CypherQuery query = mock(CypherQuery.class);
        AbstractImportTask task = taskFactory.createImportQueryTask("Networkname", query, "visualStyle");
        assertNotNull("create execute cypher-query should not return null",task);
    }
}