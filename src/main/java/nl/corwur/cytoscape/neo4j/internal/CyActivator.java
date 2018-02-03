package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.configuration.AppConfiguration;
import nl.corwur.cytoscape.neo4j.internal.neo4j.Neo4jClient;
import nl.corwur.cytoscape.neo4j.internal.ui.CommandMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.connect.ConnectInstanceMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.exportnetwork.ExportNetworkMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.importgraph.CypherQueryMenuAction;
import nl.corwur.cytoscape.neo4j.internal.ui.importgraph.querytemplate.QueryTemplateMenuAction;
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

public class CyActivator extends AbstractCyActivator  {

    private AppConfiguration appConfiguration = new AppConfiguration();

    @Override
    public void start(BundleContext context) throws Exception {
        appConfiguration.load();
        Services services = createServices(context);

        ConnectInstanceMenuAction connectAction = ConnectInstanceMenuAction.create(services);
        CypherQueryMenuAction cypherQueryMenuAction = CypherQueryMenuAction.create(services);
        QueryTemplateMenuAction queryTemplateMenuAction = QueryTemplateMenuAction.create(services);
        CommandMenuAction importGraphMenuAction = CommandMenuAction.create("Import all nodes and edges from Neo4j",services, () -> services.getCommandFactory().createImportGraphTask());
        ExportNetworkMenuAction exportNetworkToNeo4jMenuAction = ExportNetworkMenuAction.create(services);

        registerAllServices(context, connectAction, new Properties());
        registerAllServices(context, cypherQueryMenuAction, new Properties());
        registerAllServices(context, importGraphMenuAction, new Properties());
        registerAllServices(context, queryTemplateMenuAction, new Properties() );
        registerAllServices(context, exportNetworkToNeo4jMenuAction, new Properties());
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
        services.setCommandFactory(CommandFactory.create(services));
        services.setCommandExecutor(CommandExecutor.create(services));
        return services;
    }

}
