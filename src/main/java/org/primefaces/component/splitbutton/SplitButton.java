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
package org.primefaces.component.splitbutton;

import java.util.List;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import org.primefaces.model.menu.MenuModel;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
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

    public String resolveStyleClass() {
        boolean iconBlank = LangUtils.isValueBlank(getIcon());
        boolean valueBlank = getValue() == null;
        String styleClass = "";

        if (!ComponentUtils.shouldRenderChildren(this)) {
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
}