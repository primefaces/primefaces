/*
 * Copyright 2009-2012 Prime Teknoloji.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.integration.component;

import java.io.IOException;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.jsfunit.jsfsession.JSFSession;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.primefaces.integration.Deployments;
import org.primefaces.integration.bean.SpinnerView;
import org.primefaces.jsfunit.PrimeClientFactory;
import org.primefaces.jsfunit.SpinnerClient;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SpinnerTest {
    
    @Deployment
    public static WebArchive createDeployment() {
        return Deployments.createDeployment()
                    .addClass(SpinnerView.class)
                    .addAsWebResource("view/spinner.xhtml", "spinner.xhtml");
    }
    
    @Test
    public void spinnerBasic() throws IOException {
        JSFSession session = new JSFSession("/spinner.jsf");
        SpinnerClient spinner = PrimeClientFactory.spinnerClient(session.getJSFClientSession(), "form:spinnerBasic");       
        
        assertEquals("1", spinner.getValue());
        
        spinner.spinUp();
        
        assertEquals("2" ,spinner.getValue());
        
        spinner.spinDown();
        
        assertEquals("1", spinner.getValue());
        
        spinner.spinDown();

        assertEquals("0", spinner.getValue());
    }
    
    @Test
    public void spinnerAjax() throws IOException {
        JSFSession session = new JSFSession("/spinner.jsf");
        SpinnerClient spinner = PrimeClientFactory.spinnerClient(session.getJSFClientSession(), "form:spinnerAjax");       
        
        int value = (Integer) session.getJSFServerSession().getManagedBeanValue("#{spinnerView.value2}");
        assertEquals(0, value);
        
        spinner.spinUp();
        value = (Integer) session.getJSFServerSession().getManagedBeanValue("#{spinnerView.value2}");
        assertEquals(1, value);
        
        spinner.spinUp();
        value = (Integer) session.getJSFServerSession().getManagedBeanValue("#{spinnerView.value2}");
        assertEquals(2, value);
    }
}
