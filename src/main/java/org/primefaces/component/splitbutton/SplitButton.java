/**
 * Copyright 2009-2019 PrimeTek.
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
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.MenuElement;
import org.primefaces.model.menu.MenuGroup;
import org.primefaces.model.menu.MenuItem;
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

    protected MenuItem findMenuitem(List<MenuElement> elements, String id) {
        if (elements == null || elements.isEmpty()) {
            return null;
        }
        else {
            String[] paths = id.split("_");

            if (paths.length == 0) {
                return null;
            }

            int childIndex = Integer.parseInt(paths[0]);
            if (childIndex >= elements.size()) {
                return null;
            }

            MenuElement childElement = elements.get(childIndex);

            if (paths.length == 1) {
                return (MenuItem) childElement;
            }
            else {
                String relativeIndex = id.substring(id.indexOf("_") + 1);

                return findMenuitem(((MenuGroup) childElement).getElements(), relativeIndex);
            }
        }
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof MenuActionEvent) {
            FacesContext facesContext = getFacesContext();
            ELContext eLContext = facesContext.getELContext();
            MenuActionEvent menuActionEvent = (MenuActionEvent) event;
            MenuItem menuItem = menuActionEvent.getMenuItem();
            String command = menuItem.getCommand();

            if (command != null) {
                String actionExpressionString = menuItem.getCommand();
                MethodExpression noArgExpr = facesContext.getApplication().getExpressionFactory().
                        createMethodExpression(eLContext, actionExpressionString,
                                String.class, new Class[0]);
                Object invokeResult = null;

                try {
                    invokeResult = noArgExpr.invoke(eLContext, null);
                }
                catch (MethodNotFoundException methodNotFoundException) {
                    try {
                        MethodExpression argExpr = facesContext.getApplication().getExpressionFactory().
                                createMethodExpression(eLContext, actionExpressionString,
                                        String.class, new Class[]{ActionEvent.class});

                        invokeResult = argExpr.invoke(eLContext, new Object[]{event});
                    }
                    catch (MethodNotFoundException methodNotFoundException2) {
                        MethodExpression argExpr = facesContext.getApplication().getExpressionFactory().
                                createMethodExpression(eLContext, actionExpressionString,
                                        String.class, new Class[]{MenuActionEvent.class});

                        invokeResult = argExpr.invoke(eLContext, new Object[]{event});
                    }
                }
                finally {
                    String outcome = (invokeResult != null) ? invokeResult.toString() : null;

                    facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, actionExpressionString, outcome);
                }
            }

        }
        else {
            super.broadcast(event);
        }
    }
}
