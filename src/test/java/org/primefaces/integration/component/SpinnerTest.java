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

@RunWith(Arquillian.class)
public class SpinnerTest {
    
    @Deployment
    public static WebArchive createDeployment() {
        return Deployments.createDeployment()
                    .addClass(SpinnerView.class)
                    .addAsWebResource("view/spinner.xhtml", "spinner.xhtml");
    }
    
    @Test
    public void spinner() throws IOException {
        JSFSession session = new JSFSession("/spinner.jsf");
        SpinnerClient spinner = PrimeClientFactory.spinnerClient(session.getJSFClientSession(), "spinnerBasic");       
        
        Assert.assertEquals("Initial spinner value.", spinner.getValue(), "1");
        
        spinner.spinUp();
        
        Assert.assertEquals("After spin-up value.", spinner.getValue(), "2");
        
        spinner.spinDown();
        
        Assert.assertEquals("After spin-down value.", spinner.getValue(), "1");
        
        spinner.spinDown();

        Assert.assertEquals("After second spin-down value.", spinner.getValue(), "0");
    }
}
