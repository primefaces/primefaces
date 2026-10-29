/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.selectonemenu;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.component.column.Column;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.MessageFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.render.Renderer;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesComponent(value = SelectOneMenu.COMPONENT_TYPE, namespace = SelectOneMenu.COMPONENT_FAMILY)
@FacesComponentInfo(description = "SelectOneMenu is an extended version of the standard Faces SelectOneMenu.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectOneMenu extends SelectOneMenuBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectOneMenu";

    public static final String STYLE_CLASS = "p-selectonemenu p-widget p-state-default";
    public static final String RTL_CLASS = "p-selectonemenu-rtl";
    public static final String LABEL_CLASS = "p-selectonemenu-label p-inputfield";
    public static final String TRIGGER_CLASS = "p-selectonemenu-trigger p-state-default";
    public static final String TRIGGER_ICON_CLASS = "p-icon p-dropdown-icon p-selectonemenu-dropdown-icon p-c";
    public static final String PANEL_CLASS = "p-selectonemenu-panel p-widget p-widget-content p-hidden p-input-overlay";
    public static final String FOOTER_CLASS = "p-selectonemenu-footer";
    public static final String RTL_PANEL_CLASS = "p-selectonemenu-panel-rtl";
    public static final String ITEMS_WRAPPER_CLASS = "p-selectonemenu-items-wrapper";
    public static final String LIST_CLASS = "p-selectonemenu-items p-selectonemenu-list p-widget-content p-widget";
    public static final String TABLE_CLASS = "p-selectonemenu-items p-selectonemenu-table p-widget-content p-widget";
    public static final String ITEM_GROUP_CLASS = "p-selectonemenu-item-group";
    public static final String ITEM_CLASS = "p-selectonemenu-item p-selectonemenu-list-item";
    public static final String ROW_CLASS = "p-selectonemenu-item p-selectonemenu-row p-widget-content";
    public static final String FILTER_CONTAINER_CLASS = "p-selectonemenu-filter-container";
    public static final String FILTER_CLASS = "p-selectonemenu-filter p-inputfield p-inputtext p-widget p-state-default";
    public static final String FILTER_ICON_CLASS = "p-icon p-search-icon p-selectonemenu-filter-icon";

    public boolean isDynamicLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_dynamicload");
    }

    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<>(5);

        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child instanceof Column column) {
                columns.add(column);
            }
        }

        return columns;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (isAjaxBehaviorEventSource(event)) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
                Renderer<?> renderer = ComponentUtils.getUnwrappedRenderer(
                        event.getFacesContext(),
                        "jakarta.faces.SelectOne",
                        "jakarta.faces.Menu");

                Object item = renderer.getConvertedValue(event.getFacesContext(), this, getSubmittedValue());
                SelectEvent<?> selectEvent = new SelectEvent<>(this, behaviorEvent.getBehavior(), item);
                selectEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.clear)) {
                behaviorEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(behaviorEvent);
            }
            else {
                super.queueEvent(event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    protected void validateValue(FacesContext context, Object value) {
        if (isEditable()) {

            //required field validation
            if (isValid() && isRequired() && isEmpty(value)) {
                String requiredMessageStr = getRequiredMessage();
                FacesMessage message;
                if (null != requiredMessageStr) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            requiredMessageStr,
                            requiredMessageStr);
                }
                else {
                    message = MessageFactory.getFacesMessage(context, REQUIRED_MESSAGE_ID,
                            FacesMessage.SEVERITY_ERROR,
                            ComponentUtils.getLabel(context, this));
                }
                context.addMessage(getClientId(context), message);
                setValid(false);
            }

            PrimeConfiguration config = PrimeApplicationContext.getCurrentInstance(getFacesContext()).getConfig();

            //other validators
            if (isValid() && (!isEmpty(value) || config.isValidateEmptyFields())) {
                Validator[] validators = getValidators();

                for (Validator validator : validators) {
                    try {
                        validator.validate(context, this, value);
                    }
                    catch (ValidatorException ve) {
                        setValid(false);
                        FacesMessage message;
                        String validatorMessageString = getValidatorMessage();

                        if (null != validatorMessageString) {
                            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessageString, validatorMessageString);
                        }
                        else {
                            Collection<FacesMessage> messages = ve.getFacesMessages();

                            if (null != messages) {
                                message = null;
                                String cid = getClientId(context);
                                for (FacesMessage m : messages) {
                                    context.addMessage(cid, m);
                                }
                            }
                            else {
                                message = ve.getFacesMessage();
                            }
                        }

                        if (message != null) {
                            context.addMessage(getClientId(context), message);
                        }
                    }
                }
            }
        }
        else {
            super.validateValue(context, value);
        }
    }

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + (isEditable() ? "_focus" : "_label");
    }

    @Override
    public String getLabelClientId() {
        // GitHub #15009: a <label for> may only reference a labelable element.
        // The visible "_label" span isn't labelable, so point at the hidden native "_input" select instead
        // (editable mode uses the visible "_editableInput" text field).
        return getClientId(getFacesContext()) + (isEditable() ? "_editableInput" : "_input");
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }
}