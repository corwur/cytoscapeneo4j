package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.configuration.AppConfiguration;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import nl.corwur.cytoscape.neo4j.internal.tasks.ExpandNodeTask.Direction;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskExecutor;
import nl.corwur.cytoscape.neo4j.internal.tasks.TaskFactory;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectInstanceMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.expand.ConnectNodesMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.expand.ExpandNodeEdgeMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.expand.ExpandNodeLabelMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.expand.ExpandNodeMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.expand.ExpandNodesMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.exportnetwork.ExportNetworkMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.importgraph.all.ImportAllNodesAndEdgesMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.importgraph.query.ImportCypherQueryMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate.ImportQueryTemplateMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.shortestpath.ShortestPathMenuAction;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.osgi.framework.BundleContext;

import java.util.Properties;

import static org.cytoscape.work.ServiceProperties.APPS_MENU;
import static org.cytoscape.work.ServiceProperties.IN_CONTEXT_MENU;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;

/**
 * This class is the entrypoint of the application,
 * it loads the configuration,
 * creates an object that holds references to cytoscape classes,
 * adds menu items.
 */
public class CyActivator extends AbstractCyActivator {

    private AppConfiguration appConfiguration = new AppConfiguration();

    @Override
    public void start(BundleContext context) throws Exception {
        appConfiguration.load();
        Services services = createServices(context);

        ConnectInstanceMenuAction connectAction = ConnectInstanceMenuAction.create(services);
        ImportCypherQueryMenuAction importQypherQueryMenuAction = ImportCypherQueryMenuAction.create(services);
        ImportQueryTemplateMenuAction importImportQueryTemplateMenuAction = ImportQueryTemplateMenuAction.create(services);
        ImportAllNodesAndEdgesMenuAction importAllNodesAndEdgesMenuAction = ImportAllNodesAndEdgesMenuAction.create(services);
        ExportNetworkMenuAction exportNetworkToNeo4jMenuAction = ExportNetworkMenuAction.create(services);
        registerAllServices(context, connectAction, new Properties());
        registerAllServices(context, exportNetworkToNeo4jMenuAction, new Properties());

        registerAllServices(context, importQypherQueryMenuAction, new Properties());
        registerAllServices(context, importAllNodesAndEdgesMenuAction, new Properties());
        registerAllServices(context, importImportQueryTemplateMenuAction, new Properties());


        Properties expandProperties = new Properties();
        expandProperties.setProperty(PREFERRED_MENU, "Apps.Cypher Queries");
        expandProperties.setProperty(TITLE, "Connect all nodes");
        ConnectNodesMenuAction connectNodesMenuAction = ConnectNodesMenuAction.create(services, false);
        registerAllServices(context, connectNodesMenuAction, expandProperties);

        expandProperties = new Properties();
        expandProperties.setProperty(PREFERRED_MENU, "Apps.Cypher Queries");
        expandProperties.setProperty(TITLE, "Connect all selected nodes");
        connectNodesMenuAction = ConnectNodesMenuAction.create(services, true);
        registerAllServices(context, connectNodesMenuAction, expandProperties);

        expandProperties = new Properties();
        expandProperties.setProperty(PREFERRED_MENU, "Apps.Cypher Queries");
        expandProperties.setProperty(TITLE, "Expand all nodes, bidirectional");
        ExpandNodesMenuAction expandNodesMenuAction = ExpandNodesMenuAction.create(services, false, Direction.BIDIRECTIONAL);
        registerAllServices(context, expandNodesMenuAction, expandProperties);
        expandProperties.setProperty(TITLE, "Expand all nodes, incoming only");
        expandNodesMenuAction = ExpandNodesMenuAction.create(services, false, Direction.IN);
        registerAllServices(context, expandNodesMenuAction, expandProperties);
        expandProperties.setProperty(TITLE, "Expand all nodes, outgoing only");
        expandNodesMenuAction = ExpandNodesMenuAction.create(services, false, Direction.OUT);
        registerAllServices(context, expandNodesMenuAction, expandProperties);

        expandProperties = new Properties();
        expandProperties.setProperty(PREFERRED_MENU, "Apps.Cypher Queries");
        expandProperties.setProperty(TITLE, "Expand all selected nodes, bidirectional");
        expandNodesMenuAction = ExpandNodesMenuAction.create(services, true, Direction.BIDIRECTIONAL);
        registerAllServices(context, expandNodesMenuAction, expandProperties);
        expandProperties.setProperty(TITLE, "Expand all selected nodes, incoming only");
        expandNodesMenuAction = ExpandNodesMenuAction.create(services, true, Direction.IN);
        registerAllServices(context, expandNodesMenuAction, expandProperties);
        expandProperties.setProperty(TITLE, "Expand all selected nodes, outgoing only");
        expandNodesMenuAction = ExpandNodesMenuAction.create(services, true, Direction.OUT);
        registerAllServices(context, expandNodesMenuAction, expandProperties);

        Properties shortestPathProperties = new Properties();
        shortestPathProperties.setProperty(PREFERRED_MENU, "Apps.Cypher Queries");
        shortestPathProperties.setProperty(TITLE, "Get shortest paths between selected nodes");
        ShortestPathMenuAction shortestPathMenuAction = ShortestPathMenuAction.create(services);
        registerAllServices(context, shortestPathMenuAction, shortestPathProperties);

        /*
         *  Context menus
         */
        expandProperties = new Properties();
        //expandProperties.setProperty("preferredTaskManager", "menu");
        expandProperties.setProperty(PREFERRED_MENU, "Neo4j");
        expandProperties.setProperty(APPS_MENU, "Apps");
        expandProperties.setProperty(IN_CONTEXT_MENU, "true");

        /*
        expandProperties.setProperty(TITLE, "Expand node");
        ExpandNodeMenuAction expandNodeMenuAction = ExpandNodeMenuAction.create(services, false);
        registerAllServices(context, expandNodeMenuAction, expandProperties);
         */
        expandProperties.setProperty(TITLE, "Expand node, bidirectional");
        ExpandNodeMenuAction expandNodeMenuAction = ExpandNodeMenuAction.create(services, true, Direction.BIDIRECTIONAL);
        registerAllServices(context, expandNodeMenuAction, expandProperties);

        expandProperties.setProperty(TITLE, "Expand node, incoming edges");
        expandNodeMenuAction = ExpandNodeMenuAction.create(services, true, Direction.IN);
        registerAllServices(context, expandNodeMenuAction, expandProperties);

        expandProperties.setProperty(TITLE, "Expand node, outgoing edges");
        expandNodeMenuAction = ExpandNodeMenuAction.create(services, true, Direction.OUT);
        registerAllServices(context, expandNodeMenuAction, expandProperties);


        expandProperties = new Properties();
        expandProperties.setProperty(PREFERRED_MENU, "Neo4j");
        expandProperties.setProperty(IN_CONTEXT_MENU, "true");
        ExpandNodeEdgeMenuAction expandNodeEdgeMenuAction = new ExpandNodeEdgeMenuAction(services);
        registerAllServices(context, expandNodeEdgeMenuAction, expandProperties);
        ExpandNodeLabelMenuAction expandNodeLabelMenuAction = new ExpandNodeLabelMenuAction(services);
        registerAllServices(context, expandNodeLabelMenuAction, expandProperties);

    }

    private Services createServices(BundleContext context) {
        Services services = new Services();
        services.setAppConfiguration(appConfiguration);
        services.setCySwingApplication(getService(context, CySwingApplication.class));
        services.setCyApplicationManager(getService(context, CyApplicationManager.class));
        services.setCyNetworkFactory(getService(context, CyNetworkFactory.class));
        services.setCyNetworkManager(getService(context, CyNetworkManager.class));
        services.setCyNetworkViewManager(getService(context, CyNetworkViewManager.class));
        services.setDialogTaskManager(getService(context, DialogTaskManager.class));
        services.setCyNetworkViewFactory(getService(context, CyNetworkViewFactory.class));
        services.setCyLayoutAlgorithmManager(getService(context, CyLayoutAlgorithmManager.class));
        services.setVisualMappingManager(getService(context, VisualMappingManager.class));
        services.setCyEventHelper(getService(context, CyEventHelper.class));
        services.setVisualStyleFactory(getService(context, VisualStyleFactory.class));
        services.setNeo4jClient(new Neo4jClient());
        services.setTaskFactory(TaskFactory.create(services));
        services.setTaskExecutor(TaskExecutor.create(services));
        return services;
    }

}
