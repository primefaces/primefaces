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
package org.primefaces.component.splitbutton;

import org.primefaces.model.menu.MenuModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import java.util.List;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SplitButton extends SplitButtonBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SplitButton";

    public static final String STYLE_CLASS = "ui-splitbutton ui-buttonset ui-widget";
    public static final String BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-icon-left";
    public static final String BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-icon-right";
    public static final String MENU_ICON_BUTTON_CLASS = "ui-splitbutton-menubutton  ui-button ui-widget ui-state-default ui-corner-right ui-button-icon-only";
    public static final String BUTTON_TEXT_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-only";
    public static final String BUTTON_ICON_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-icon-only";
    public static final String SPLITBUTTON_CONTAINER_CLASS = "ui-menu ui-splitbuttonmenu ui-menu-dynamic ui-widget ui-widget-content ui-corner-all ui-helper-clearfix ui-shadow";
    public static final String LIST_WRAPPER_CLASS = "ui-splitbuttonmenu-list-wrapper";

    private String confirmationScript;

    public String resolveStyleClass() {
        boolean iconBlank = LangUtils.isValueBlank(getIcon());
        boolean valueBlank = getValue() == null;
        boolean modelBlank = getModel() == null;
        String styleClass = "";

        if (modelBlank && !ComponentUtils.shouldRenderChildren(this)) {
            if (!valueBlank && iconBlank) {
                styleClass = HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
            }
            else if (!valueBlank && !iconBlank) {
                styleClass = getIconPos().equals("left")
                             ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS
                             : HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
            }
            else if (valueBlank && !iconBlank) {
                styleClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
            }
        }
        else if (!valueBlank && iconBlank) {
            styleClass = BUTTON_TEXT_ONLY_BUTTON_CLASS;
        }
        else if (!valueBlank && !iconBlank) {
            styleClass = getIconPos().equals("left") ? BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
        }
        else if (valueBlank && !iconBlank) {
            styleClass = BUTTON_ICON_ONLY_BUTTON_CLASS;
        }

        if (isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        }

        return styleClass;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered() || isDisabled()) {
            return;
        }
        super.processDecodes(context);
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    @Override
    public boolean isAjaxified() {
        return isAjax();
    }

    @Override
    public List getElements() {
        MenuModel model = getModel();
        if (model != null) {
            return model.getElements();
        }
        else {
            return getChildren();
        }
    }

    public int getElementsCount() {
        List elements = getElements();

        return (elements == null) ? 0 : elements.size();
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        broadcastMenuActionEvent(event, getFacesContext(), super::broadcast);
    }

    @Override
    public boolean requiresConfirmation() {
        return confirmationScript != null;
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
    public String getDefaultEventName() {
        return "click";
    }
}
