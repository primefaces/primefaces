/*
 * Copyright 2009-2015 PrimeTek.
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
package org.primefaces;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.faces.FactoryFinder;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIPanel;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.mock.FacesContextMock;
import org.primefaces.mock.TestVisitContextFactory;

public class PrimeFacesTest {

    @Before
    public void setup() {
        Map<Object, Object> attributes = new HashMap<Object, Object>();
        attributes.put(UINamingContainer.SEPARATOR_CHAR_PARAM_NAME, ':');

        FacesContext context = new FacesContextMock(attributes);
        context.setViewRoot(new UIViewRoot());
        FactoryFinder.setFactory(FactoryFinder.VISIT_CONTEXT_FACTORY, TestVisitContextFactory.class.getName());
    }


    @Test
    public void update() {
        UIComponent root = new UIPanel();
        root.setId("root");
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

        UIComponent command1 = new UICommand();
        command1.setId("command1");
        root.getChildren().add(command1);

        PrimeFaces.current().ajax().update("command1");
        PrimeFaces.current().ajax().update(":command1");

        Collection<String> renderIds = FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();

        Assert.assertEquals(2, renderIds.size());
    }

    @Test
    public void update_ComponentNotFound() {
        UIComponent root = new UIPanel();
        root.setId("root");
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

        try {
            PrimeFaces.current().ajax().update("doesnTExist");
        }
        catch (Exception e) {
            Assert.fail("This should actually NOT raise an exception");
        }
    }

    @Test
    public void update_Multiple() {
        UIComponent root = new UIPanel();
        root.setId("root");
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

        UIComponent command1 = new UICommand();
        command1.setId("command1");
        root.getChildren().add(command1);

        PrimeFaces.current().ajax().update(Arrays.asList("command1", ":command1"));

        Collection<String> renderIds = FacesContext.getCurrentInstance().getPartialViewContext().getRenderIds();

        Assert.assertEquals(2, renderIds.size());
    }

    @Test
    public void update_Multiple_ComponentNotFound() {
        UIComponent root = new UIPanel();
        root.setId("root");
        FacesContext.getCurrentInstance().getViewRoot().getChildren().add(root);

        try {
            PrimeFaces.current().ajax().update(Arrays.asList("doesnTExist"));
        }
        catch (Exception e) {
            Assert.fail("This should actually NOT raise an exception");
        }
    }
}
