/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
