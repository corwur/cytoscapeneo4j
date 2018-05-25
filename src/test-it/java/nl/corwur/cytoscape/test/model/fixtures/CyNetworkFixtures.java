package nl.corwur.cytoscape.test.model.fixtures;

import org.cytoscape.equations.EquationCompiler;
import org.cytoscape.equations.Interpreter;
import org.cytoscape.equations.internal.EquationCompilerImpl;
import org.cytoscape.equations.internal.EquationParserImpl;
import org.cytoscape.equations.internal.interpreter.InterpreterImpl;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.event.DummyCyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.internal.CyNetworkManagerImpl;
import org.cytoscape.model.internal.CyNetworkTableManagerImpl;
import org.cytoscape.model.internal.CyRootNetworkImpl;
import org.cytoscape.model.internal.CyTableFactoryImpl;
import org.cytoscape.model.internal.CyTableManagerImpl;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CyNetworkFixtures {

    public static final String MY_NAME = "my_name";

    public enum CyFixture {
        NETWORK_WITH_3_NODES_1_EDGE(createNetworkWith3NodesAnd1Edge());

        private final CyNetwork network;

        CyFixture(CyNetwork network) {
            this.network = network;
        }

        public CyNetwork getNetwork() {
            return network;
        }
    }

    private static CyNetwork createNetworkWith3NodesAnd1Edge() {
        CyRootNetwork cyRootNetwork = getPublicRootInstance(new DummyCyEventHelper(), SavePolicy.SESSION_FILE);
        CyNetwork cyNetwork = cyRootNetwork.addSubNetwork();
        cyNetwork.getDefaultNodeTable().createColumn(MY_NAME, String.class, false);
        cyNetwork.getDefaultNodeTable().createListColumn("list", String.class, false);
        CyNode a = cyNetwork.addNode();
        CyNode b = cyNetwork.addNode();
        CyNode c = cyNetwork.addNode();
        cyNetwork.addEdge(a,b, true);
        cyNetwork.getRow(a).set(MY_NAME, "a");
        cyNetwork.getRow(b).set(MY_NAME, "b");
        cyNetwork.getRow(c).set(MY_NAME, "c");
        return cyNetwork;
    }

    public static CyNetwork emptyNetwork() {
        CyRootNetwork cyRootNetwork = getPublicRootInstance(new DummyCyEventHelper(), SavePolicy.SESSION_FILE);
        return cyRootNetwork.addSubNetwork();
    }


    public static CyRootNetwork getPublicRootInstance(DummyCyEventHelper deh, SavePolicy policy) {
        CyServiceRegistrar serviceRegistrar = mockCyServiceRegistrar(deh);

        final CyNetworkTableManagerImpl ntm = new CyNetworkTableManagerImpl();
        final CyTableManagerImpl tm = new CyTableManagerImpl(ntm, new CyNetworkManagerImpl(serviceRegistrar),
                serviceRegistrar);

        final CyTableFactoryImpl tableFactory = new CyTableFactoryImpl(deh, serviceRegistrar);

        return new CyRootNetworkImpl(deh, tm, ntm, tableFactory, serviceRegistrar, true, policy);
    }

    private static CyServiceRegistrar mockCyServiceRegistrar(CyEventHelper deh) {
        CyServiceRegistrar serviceRegistrar = mock(CyServiceRegistrar.class);
        CyNetworkNaming namingUtil = mock(CyNetworkNaming.class);
        EquationCompiler compiler = new EquationCompilerImpl(new EquationParserImpl(serviceRegistrar));
        final Interpreter interpreter = new InterpreterImpl();

        when(serviceRegistrar.getService(CyEventHelper.class)).thenReturn(deh);
        when(serviceRegistrar.getService(CyNetworkNaming.class)).thenReturn(namingUtil);
        when(serviceRegistrar.getService(EquationCompiler.class)).thenReturn(compiler);
        when(serviceRegistrar.getService(Interpreter.class)).thenReturn(interpreter);

        return serviceRegistrar;
    }

}
