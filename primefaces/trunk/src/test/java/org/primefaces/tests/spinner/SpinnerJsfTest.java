package org.primefaces.tests.spinner;

import java.io.IOException;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.primefaces.jsfunit.SpinnerClient;
import org.primefaces.tests.BaseJsfTest;
import org.primefaces.tests.BaseRequestListener;
import org.primefaces.tests.TestController;

public class SpinnerJsfTest extends BaseJsfTest<SpinnerJsfTest> {

    protected static final String DEFAULT_WEB_ARCHIVE_NAME = "spinnerTest.war";
    
    @Deployment
    public static WebArchive deploy() {
        return getBaseWebArchive().addClass(TestController.class)
            .addAsWebResource("view/spinner.xhtml", "spinner.xhtml")
            .addAsWebResource("faces-config.xml");
    }
    
    @Test
    public void shouldInitTests() throws IOException {
        System.out.println("Test Inits..");
//        System.out.println(PrimePartialResponseWriter.RENDER_ALL_MARKER);
        SpinnerClient spinner = setTestingURL("/spinner.jsf").getClientFactory().spinnerClient().setComponentID("spinnerBasic");
        
//        SpinnerClient spinner = PrimeClientFactory.spinnerClient(session.getJSFClientSession()).setComponentID("spinnerBasic");
        
        addRequestListener(new BaseRequestListener());
        
        Assert.assertEquals("Initial spinner value.", spinner.getValue(), "1");
        
        spinner.spinUp();
        
        Assert.assertEquals("After spin-up value.", spinner.getValue(), "2");
        
        spinner.spinDown();
        
        Assert.assertEquals("After spin-down value.", spinner.getValue(), "1");
        
        spinner.spinDown();

        Assert.assertEquals("After second spin-down value.", spinner.getValue(), "0");
    }
}
