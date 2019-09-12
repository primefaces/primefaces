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
package org.primefaces.component.commandbutton;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.DialogReturnAware;

import org.primefaces.event.SelectEvent;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MapBuilder;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class CommandButton extends CommandButtonBase implements DialogReturnAware {

    public static final String COMPONENT_TYPE = "org.primefaces.component.CommandButton";

    private static final Logger LOGGER = Logger.getLogger(CommandButton.class.getName());

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = MapBuilder.<String, Class<? extends BehaviorEvent>>builder()
            .put("click", null)
            .put(EVENT_DIALOG_RETURN, SelectEvent.class)
            .build();
    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();
    private String confirmationScript;

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return "click";
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        if (isDialogReturnEvent(event, context)) {
            queueDialogReturnEvent(event, context, this, super::queueEvent);
        }
        else {
            super.queueEvent(event);
        }
    }

    public String resolveStyleClass() {
        String icon = getIcon();
        Object value = getValue();
        String styleClass = "";

        if (value != null && LangUtils.isValueBlank(icon)) {
            styleClass = HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
        }
        else if (value != null && !LangUtils.isValueBlank(icon)) {
            styleClass = getIconPos().equals("left") ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
        }
        else if (value == null && !LangUtils.isValueBlank(icon)) {
            styleClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
        }

        if (isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }

        String userStyleClass = getStyleClass();
        if (userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }

        return styleClass;
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    @Override
    public String getConfirmationScript() {
        return confirmationScript;
    }

    @Override
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }

    @Override
    public boolean requiresConfirmation() {
        return confirmationScript != null;
    }

    @Override
    public boolean isAjaxified() {
        return !getType().equals("reset") && !getType().equals("button") && isAjax();
    }
}