package nl.corwur.cytoscape.neo4j.internal;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CyActivatorTest {
    @Test
    public void start() throws Exception {
        BundleContext context = mock(BundleContext.class);
        when(context.getServiceReference(anyString())).thenReturn(mock(ServiceReference.class));
        //        when(context.getServiceReference(anyString())).then(new Answer<Object>() {
//            @Override
//            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
//                return mock(getClass().getClassLoader().loadClass((String) invocationOnMock.getArguments()[0]));
//            }
//        });

        CyActivator cyActivator = new CyActivator();
        cyActivator.start(context);

    }

}