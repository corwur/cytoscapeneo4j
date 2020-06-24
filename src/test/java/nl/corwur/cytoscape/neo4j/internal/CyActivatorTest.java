package nl.corwur.cytoscape.neo4j.internal;

import nl.corwur.cytoscape.neo4j.internal.configuration.AppConfiguration;
import org.apache.sling.testing.mock.osgi.MockOsgi;
import org.apache.sling.testing.mock.osgi.junit.OsgiContext;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.swing.DialogTaskManager;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class CyActivatorTest {

    @Mock
    private CySwingApplication cySwingApplication;

    @Mock
    private CyApplicationManager cyApplicationManager;

    @Mock
    private CyNetworkFactory cyNetworkFactory;

    @Mock
    private CyNetworkManager cyNetworkManager;
    @Mock
    private CyNetworkViewManager cyNetworkViewManager;
    @Mock
    private DialogTaskManager dialogTaskManager;
    @Mock
    private CyNetworkViewFactory cyNetworkViewFactory;
    @Mock
    private CyLayoutAlgorithmManager cyLayoutAlgorithmManager;
    @Mock
    private VisualMappingManager visualMappingManager;
    @Mock
    private CyEventHelper cyEventHelper;
    @Mock
    private VisualStyleFactory visualStyleFactory;

    @Rule
    public final OsgiContext context = new OsgiContext();

    @Test
    public void start() throws Exception {
        BundleContext bundleContext = MockOsgi.newBundleContext();
        bundleContext.registerService(CySwingApplication.class, cySwingApplication, null);
        bundleContext.registerService(CyApplicationManager.class, cyApplicationManager, null);
        bundleContext.registerService(CyNetworkFactory.class, cyNetworkFactory, null);

        bundleContext.registerService(CyNetworkManager.class,cyNetworkManager, null);
        bundleContext.registerService(CyNetworkViewManager.class,cyNetworkViewManager, null);
        bundleContext.registerService(DialogTaskManager.class, dialogTaskManager, null);
        bundleContext.registerService(CyNetworkViewFactory.class, cyNetworkViewFactory, null);
        bundleContext.registerService(CyLayoutAlgorithmManager.class, cyLayoutAlgorithmManager, null);
        bundleContext.registerService(VisualMappingManager.class, visualMappingManager, null);
        bundleContext.registerService(CyEventHelper.class, cyEventHelper, null);
        bundleContext.registerService(VisualStyleFactory.class, visualStyleFactory, null);

        CyActivator cyActivator = new CyActivator();
        cyActivator.start(bundleContext);

        AppConfiguration appConfiguration = (AppConfiguration) getField(CyActivator.class,"appConfiguration").get(cyActivator);

        assertEquals("localhost", appConfiguration.getNeo4jHost());
        assertEquals("neo4j", appConfiguration.getNeo4jUsername());
        assertEquals("", appConfiguration.getTemplateDirectory());

        Map<?,?> serviceRegistrations = (Map<?,?>) getField(AbstractCyActivator.class, "serviceRegistrations").get(cyActivator);
        List<?> serviceListeners = (List<?>) getField(AbstractCyActivator.class, "serviceListeners").get(cyActivator);
        List<ServiceReference> gottenServices = (List<ServiceReference>) getField(AbstractCyActivator.class, "gottenServices").get(cyActivator);

        assertEquals(4, serviceRegistrations.size());
        assertEquals(0, serviceListeners.size());
        assertEquals(11, gottenServices.size());

    }

    private <T> Field getField(Class<T> aClass, String name) throws NoSuchFieldException {
        Field field = aClass.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

}