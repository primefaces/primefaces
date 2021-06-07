/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.selenium.internal.component;

import java.util.UUID;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.primefaces.selenium.internal.ConfigProvider;

public class PrimeFacesSeleniumDummyComponent extends UIComponentBase {

    public PrimeFacesSeleniumDummyComponent() {
        FacesContext context = FacesContext.getCurrentInstance();
        Application application = context.getApplication();
        if (ConfigProvider.getInstance().isDisableAnimations()) {
            addResource(context, application, "pfselenium.disableanimations.css");
        }
        addResource(context, application, "pfselenium.core.csp.js");
    }

    private void addResource(FacesContext context, Application application, String resourceName) {
        UIComponent componentResource = application.createComponent("javax.faces.Output");
        componentResource.setId("pfs-" + UUID.randomUUID().toString());
        componentResource.setRendererType(application.getResourceHandler().getRendererTypeForResourceName(resourceName));
        componentResource.getAttributes().put("name", resourceName);
        componentResource.getAttributes().put("library", "primefaces_selenium");
        componentResource.getAttributes().put("target", "head");
        context.getViewRoot().addComponentResource(context, componentResource, "head");
    }

    @Override
    public String getFamily() {
        return "org.primefaces.selenium";
    }
}
