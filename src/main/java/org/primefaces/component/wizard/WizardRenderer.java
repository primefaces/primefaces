/**
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
package org.primefaces.component.wizard;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.PhaseId;

import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class WizardRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Wizard wizard = (Wizard) component;

        if (ComponentUtils.isRequestSource(wizard, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = wizard.getClientId(context);
            String stepToGo = params.get(clientId + "_stepToGo");
            String currentStep = wizard.getStep();

            FlowEvent event = new FlowEvent(wizard, currentStep, stepToGo);
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);

            wizard.queueEvent(event);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Wizard wizard = (Wizard) component;

        if (ComponentUtils.isRequestSource(wizard, context)) {
            encodeStep(context, wizard);
        }
        else {
            encodeMarkup(context, wizard);
            encodeScript(context, wizard);
        }
    }

    protected void encodeStep(FacesContext context, Wizard wizard) throws IOException {
        String stepToDisplay = wizard.getStep();

        if (!isValueBlank(stepToDisplay)) {
            UIComponent tabToDisplay = null;
            for (UIComponent child : wizard.getChildren()) {
                if (child.getId().equals(stepToDisplay)) {
                    tabToDisplay = child;
                }
            }

            if (tabToDisplay != null) {
                tabToDisplay.encodeAll(context);
            }

            PrimeFaces.current().ajax().addCallbackParam("currentStep", wizard.getStep());
        }
    }

    protected void encodeScript(FacesContext context, Wizard wizard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = wizard.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(context, wizard);
        if (form == null) {
            throw new FacesException("Wizard : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Wizard", wizard.resolveWidgetVar(context), clientId)
                .attr("showStepStatus", wizard.isShowStepStatus())
                .attr("showNavBar", wizard.isShowNavBar());

        if (wizard.getOnback() != null) {
            wb.callback("onback", "function()", wizard.getOnback());
        }
        if (wizard.getOnnext() != null) {
            wb.callback("onnext", "function()", wizard.getOnnext());
        }

        //all steps
        writer.write(",steps:[");
        boolean firstStep = true;
        String defaultStep = null;
        for (Iterator<UIComponent> children = wizard.getChildren().iterator(); children.hasNext(); ) {
            UIComponent child = children.next();

            if (child instanceof Tab && child.isRendered()) {
                Tab tab = (Tab) child;
                if (defaultStep == null) {
                    defaultStep = tab.getId();
                }

                if (!firstStep) {
                    writer.write(",");
                }
                else {
                    firstStep = false;
                }

                writer.write("'" + tab.getId() + "'");
            }
        }
        writer.write("]");

        //current step
        if (wizard.getStep() == null) {
            wizard.setStep(defaultStep);
        }

        wb.attr("initialStep", wizard.getStep());

        encodeClientBehaviors(context, wizard);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext facesContext, Wizard wizard) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = wizard.getClientId(facesContext);
        String styleClass = wizard.getStyleClass() == null ? "ui-wizard ui-widget" : "ui-wizard ui-widget " + wizard.getStyleClass();

        writer.startElement("div", wizard);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (wizard.getStyle() != null) {
            writer.writeAttribute("style", wizard.getStyle(), "style");
        }

        if (wizard.isShowStepStatus()) {
            encodeStepStatus(facesContext, wizard);
        }

        encodeContent(facesContext, wizard);

        if (wizard.isShowNavBar()) {
            encodeNavigators(facesContext, wizard);
        }

        writer.endElement("div");
    }

    protected void encodeCurrentStep(FacesContext facesContext, Wizard wizard) throws IOException {
        for (UIComponent child : wizard.getChildren()) {
            if (child instanceof Tab && child.isRendered()) {
                Tab tab = (Tab) child;

                if ((wizard.getStep() == null || tab.getId().equals(wizard.getStep()))) {
                    tab.encodeAll(facesContext);

                    break;
                }
            }
        }
    }

    protected void encodeNavigators(FacesContext facesContext, Wizard wizard) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = wizard.getClientId(facesContext);

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-wizard-navbar ui-helper-clearfix", null);

        encodeNavigator(facesContext, wizard, clientId + "_back", wizard.getBackLabel(), Wizard.BACK_BUTTON_CLASS, "ui-icon-arrowthick-1-w");
        encodeNavigator(facesContext, wizard, clientId + "_next", wizard.getNextLabel(), Wizard.NEXT_BUTTON_CLASS, "ui-icon-arrowthick-1-e");

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext facesContext, Wizard wizard) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = wizard.getClientId(facesContext);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", "ui-wizard-content", null);

        encodeCurrentStep(facesContext, wizard);

        writer.endElement("div");
    }

    protected void encodeStepStatus(FacesContext context, Wizard wizard) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String currentStep = wizard.getStep();
        boolean currentFound = false;

        writer.startElement("ul", null);
        writer.writeAttribute("class", Wizard.STEP_STATUS_CLASS, null);

        for (UIComponent child : wizard.getChildren()) {
            if (child instanceof Tab && child.isRendered()) {
                Tab tab = (Tab) child;
                String title = tab.getTitle();
                UIComponent titleFacet = tab.getFacet("title");
                boolean active = (!currentFound) && (currentStep == null || tab.getId().equals(currentStep));
                String titleStyleClass = active ? Wizard.ACTIVE_STEP_CLASS : Wizard.STEP_CLASS;
                if (tab.getTitleStyleClass() != null) {
                    titleStyleClass = titleStyleClass + " " + tab.getTitleStyleClass();
                }

                if (active) {
                    currentFound = true;
                }

                writer.startElement("li", null);
                writer.writeAttribute("class", titleStyleClass, null);
                if (tab.getTitleStyle() != null) {
                    writer.writeAttribute("style", tab.getTitleStyle(), null);
                }
                if (tab.getTitletip() != null) {
                    writer.writeAttribute("title", tab.getTitletip(), null);
                }

                if (titleFacet != null) {
                    titleFacet.encodeAll(context);
                }
                else if (title != null) {
                    writer.writeText(title, null);
                }

                writer.endElement("li");
            }
        }

        writer.endElement("ul");
    }

    protected void encodeNavigator(FacesContext facesContext, Wizard wizard, String id, String label, String buttonClass, String icon) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();

        writer.startElement("button", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS + " " + buttonClass, null);

        //button icon
        String iconClass = HTML.BUTTON_LEFT_ICON_CLASS + " " + icon;
        writer.startElement("span", null);
        writer.writeAttribute("class", iconClass, null);
        writer.endElement("span");

        //text
        writer.startElement("span", null);
        writer.writeAttribute("class", HTML.BUTTON_TEXT_CLASS, null);
        writer.writeText(label, "value");
        writer.endElement("span");

        writer.endElement("button");
    }

    @Override
    public void encodeChildren(FacesContext facesContext, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
