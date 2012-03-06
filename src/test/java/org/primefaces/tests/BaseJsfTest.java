package org.primefaces.tests;

import java.io.IOException;
import javax.faces.context.FacesContext;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.framework.JSFUnitWebConnection;
import org.jboss.jsfunit.framework.RequestListener;
import org.jboss.jsfunit.jsfsession.JSFClientSession;
import org.jboss.jsfunit.jsfsession.JSFServerSession;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;
import org.primefaces.jsfunit.PrimeClientFactory;

@RunWith(Arquillian.class)
public abstract class BaseJsfTest < T extends BaseJsfTest > {
    
    
    protected static final String DEFAULT_WEB_ARCHIVE_NAME = "test.war";
    
    protected JSFSession session;
    
    protected PrimeClientFactory clientFactory;
    
    protected String testingURL;

    protected static WebArchive webArchive;
    
    protected static WebArchive getBaseWebArchive() {
        if(webArchive == null) {
            webArchive = ShrinkWrap.create(WebArchive.class, BaseJsfTest.DEFAULT_WEB_ARCHIVE_NAME)
                .setWebXML("jsf-web.xml");
        }
        
        return webArchive;
    }
    
    protected FacesContext getFacesContext() throws IOException {
        return getServer().getFacesContext();
    }
     
    protected JSFServerSession getServer() throws IOException {
        return getSession().getJSFServerSession();
    }
    
    protected JSFClientSession getBaseClient() throws IOException {
        return getSession().getJSFClientSession();
    }
    
    protected PrimeClientFactory getClientFactory() throws IOException {
        if(clientFactory == null) {
            clientFactory = new PrimeClientFactory(getBaseClient());
        }
        
        return clientFactory;
    }
    
    protected JSFSession getSession() throws IOException {
        if(session == null) {
            session = new JSFSession(getTestingURL());
        }
        
//        JSFUnitWebConnection a = new JSFUnitWebConnection(null);
//        
//        JSFUnitTestEnricher b = new JSFUnitTestEnricher();
//        
//        JSFUnitHttpServletRequest c = new JSFUnitHttpServletRequest(null, null);
        
//        session.getWebClient().getWebConnection().
        return session;
    }
    
    protected T addRequestListener(RequestListener requestListener) throws IOException {
        ((JSFUnitWebConnection)getSession().getWebClient().getWebConnection()).addListener(requestListener);
        
        return (T) this;
    }
    
    protected T removeRequestListener(RequestListener requestListener) throws IOException {
        ((JSFUnitWebConnection)getSession().getWebClient().getWebConnection()).removeListener(requestListener);
        
        return (T) this;
    }
    
    protected T setSession(JSFSession s) {
        this.session = s;
        return (T) this;
    }
    
    protected T refresh() throws IOException {
        setSession(null);
        return (T) this;
    }
    
    protected T refresh(String newURL) throws IOException {
        setTestingURL(newURL);
        return refresh();
    }
    
    public String getTestingURL() {
        return testingURL;
    }

    public T setTestingURL(String testingURL) {
        this.testingURL = testingURL;
        return (T) this;
    }
}