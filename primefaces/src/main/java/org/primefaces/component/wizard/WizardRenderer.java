/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentTraversalUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIForm;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.event.PhaseId;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = Wizard.DEFAULT_RENDERER, componentFamily = Wizard.COMPONENT_FAMILY)
public class WizardRenderer extends CoreRenderer<Wizard> {

    @Override
    public void decode(FacesContext context, Wizard component) {
        if (ComponentUtils.isRequestSource(component, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = component.getClientId(context);
            String stepToGo = params.get(clientId + "_stepToGo");
            String currentStep = component.getStep();

            FlowEvent event = new FlowEvent(component, currentStep, stepToGo);
            event.setPhaseId(PhaseId.INVOKE_APPLICATION);

            component.queueEvent(event);
        }

        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, Wizard component) throws IOException {
        if (ComponentUtils.isRequestSource(component, context)) {
            encodeStep(context, component);
        }
        else {
            encodeMarkup(context, component);
            encodeScript(context, component);
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

    protected void encodeScript(FacesContext context, Wizard component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        UIForm form = ComponentTraversalUtils.closestForm(component);
        if (form == null) {
            throw new FacesException("Wizard : \"" + clientId + "\" must be inside a form element");
        }

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Wizard", component)
                .attr("showStepStatus", component.isShowStepStatus())
                .attr("showNavBar", component.isShowNavBar())
                .attr("disableOnAjax", component.isDisableOnAjax(), true)
                .attr("highlightCompletedSteps", component.isHighlightCompletedSteps(), false);

        String effect = component.getEffect();
        if (effect != null) {
            wb.attr("effect", effect, null)
                .attr("effectDuration", component.getEffectDuration(), Integer.MAX_VALUE);
        }

        if (component.getOnback() != null) {
            wb.callback("onback", "function()", component.getOnback());
        }
        if (component.getOnnext() != null) {
            wb.callback("onnext", "function()", component.getOnnext());
        }

        //all steps
        writer.write(",steps:[");
        boolean firstStep = true;
        String defaultStep = null;
        for (Iterator<UIComponent> children = component.getChildren().iterator(); children.hasNext(); ) {
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
        if (component.getStep() == null) {
            component.setStep(defaultStep);
        }

        wb.attr("initialStep", component.getStep());

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeMarkup(FacesContext facesContext, Wizard component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = component.getClientId(facesContext);
        String styleClass = getStyleClassBuilder(facesContext)
                .add("ui-wizard ui-widget")
                .add(component.getStyleClass())
                .build();

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", styleClass, "styleClass");
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), "style");
        }

        if (component.isShowStepStatus()) {
            encodeStepStatus(facesContext, component);
        }

        encodeContent(facesContext, component);

        if (component.isShowNavBar()) {
            encodeNavigators(facesContext, component);
        }

        writer.endElement("div");
    }

    protected void encodeCurrentStep(FacesContext facesContext, Wizard component) throws IOException {
        for (UIComponent child : component.getChildren()) {
            if (child instanceof Tab && child.isRendered()) {
                Tab tab = (Tab) child;

                if ((component.getStep() == null || tab.getId().equals(component.getStep()))) {
                    tab.encodeAll(facesContext);

                    break;
                }
            }
        }
    }

    protected void encodeNavigators(FacesContext facesContext, Wizard component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = component.getClientId(facesContext);

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-wizard-navbar ui-helper-clearfix", null);

        encodeNavigator(facesContext, component, clientId + "_back", component.getBackLabel(), Wizard.BACK_BUTTON_CLASS, "ui-icon-arrowthick-1-w", true);
        encodeNavigator(facesContext, component, clientId + "_next", component.getNextLabel(), Wizard.NEXT_BUTTON_CLASS, "ui-icon-arrowthick-1-e", false);

        writer.endElement("div");
    }

    protected void encodeContent(FacesContext facesContext, Wizard component) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String clientId = component.getClientId(facesContext);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_content", "id");
        writer.writeAttribute("class", "ui-wizard-content", null);

        encodeCurrentStep(facesContext, component);

        writer.endElement("div");
    }

    protected void encodeStepStatus(FacesContext context, Wizard component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String currentStep = component.getStep();
        boolean currentFound = false;

        writer.startElement("ul", null);
        writer.writeAttribute("class", Wizard.STEP_STATUS_CLASS, null);

        for (UIComponent child : component.getChildren()) {
            if (child instanceof Tab && child.isRendered()) {
                Tab tab = (Tab) child;
                String title = tab.getTitle();
                UIComponent titleFacet = tab.getFacet("title");
                boolean active = (!currentFound) && (currentStep == null || tab.getId().equals(currentStep));
                String titleStyleClass = getStyleClassBuilder(context)
                        .add(active, Wizard.ACTIVE_STEP_CLASS, Wizard.STEP_CLASS)
                        .add(tab.getTitleStyleClass())
                        .build();

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

                if (FacetUtils.shouldRenderFacet(titleFacet)) {
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

    protected void encodeNavigator(FacesContext facesContext, Wizard component, String id, String label, String buttonClass,
                                   String icon, boolean isBack) throws IOException {
        ResponseWriter writer = facesContext.getResponseWriter();
        String iconClass = getStyleClassBuilder(facesContext)
                .add(isBack, HTML.BUTTON_LEFT_ICON_CLASS, HTML.BUTTON_RIGHT_ICON_CLASS)
                .add(icon)
                .build();
        String button_Class = getStyleClassBuilder(facesContext)
                .add(isBack, HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS, HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS)
                .add(buttonClass)
                .build();

        writer.startElement("button", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "button", null);
        writer.writeAttribute("class", button_Class, null);

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
    public void encodeChildren(FacesContext facesContext, Wizard component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
