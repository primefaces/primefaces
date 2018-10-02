/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.component.selectonemenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.primefaces.component.column.Column;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class SelectOneMenu extends SelectOneMenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectOneMenu";

    public static final String STYLE_CLASS = "ui-selectonemenu ui-widget ui-state-default ui-corner-all";
    public static final String LABEL_CLASS = "ui-selectonemenu-label ui-inputfield ui-corner-all";
    public static final String TRIGGER_CLASS = "ui-selectonemenu-trigger ui-state-default ui-corner-right";
    public static final String PANEL_CLASS = "ui-selectonemenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-shadow ui-input-overlay";
    public static final String ITEMS_WRAPPER_CLASS = "ui-selectonemenu-items-wrapper";
    public static final String LIST_CLASS = "ui-selectonemenu-items ui-selectonemenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public static final String TABLE_CLASS = "ui-selectonemenu-items ui-selectonemenu-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public static final String ITEM_GROUP_CLASS = "ui-selectonemenu-item-group ui-corner-all";
    public static final String ITEM_CLASS = "ui-selectonemenu-item ui-selectonemenu-list-item ui-corner-all";
    public static final String ROW_CLASS = "ui-selectonemenu-item ui-selectonemenu-row ui-widget-content";
    public static final String FILTER_CONTAINER_CLASS = "ui-selectonemenu-filter-container";
    public static final String FILTER_CLASS = "ui-selectonemenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_ICON_CLASS = "ui-icon ui-icon-search";

    private static final Collection<String> EVENT_NAMES = LangUtils.unmodifiableList("itemSelect", "blur", "change", "valueChange", "click",
            "dblclick", "focus", "keydown", "keypress", "keyup", "mousedown", "mousemove", "mouseout", "mouseover", "mouseup", "select");

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    public boolean isDynamicLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_dynamicload");
    }

    @Override
    public String getDefaultEventName() {
        return "valueChange";
    }

    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<>();

        for (UIComponent kid : getChildren()) {
            if (kid instanceof Column) {
                columns.add((Column) kid);
            }
        }

        return columns;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (event instanceof AjaxBehaviorEvent) {
            FacesContext context = getFacesContext();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if ("itemSelect".equals(eventName)) {
                Renderer renderer = ComponentUtils.getUnwrappedRenderer(
                        context,
                        "javax.faces.SelectOne",
                        "javax.faces.Menu",
                        Renderer.class);

                Object item = renderer.getConvertedValue(context, this, getSubmittedValue());
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), item);
                selectEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(selectEvent);
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
                    message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{MessageFactory.getLabel(context, this)});
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
        return getClientId(getFacesContext()) + "_focus";
    }

    @Override
    public String getValidatableInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }


}