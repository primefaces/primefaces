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
package org.primefaces.component.splitbutton;

import java.util.List;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
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
import org.primefaces.util.SerializableFunction;

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
                String relativeIndex = id.substring(id.indexOf('_') + 1);

                return findMenuitem(((MenuGroup) childElement).getElements(), relativeIndex);
            }
        }
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof MenuActionEvent) {
            FacesContext facesContext = getFacesContext();

            MenuActionEvent menuActionEvent = (MenuActionEvent) event;
            MenuItem menuItem = menuActionEvent.getMenuItem();

            SerializableFunction<MenuItem, String> function = menuItem.getFunction();
            String command = menuItem.getCommand();
            if (function != null) {
                String outcome = function.apply(menuItem);
                facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
            }
            else if (command != null) {
                ELContext elContext = facesContext.getELContext();
                ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();

                Object invokeResult = null;
                try {
                    MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                                String.class, new Class[0]);
                    invokeResult = me.invoke(elContext, null);
                }
                catch (MethodNotFoundException mnfe1) {
                    try {
                        MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                                        String.class, new Class[]{ActionEvent.class});
                        invokeResult = me.invoke(elContext, new Object[]{event});
                    }
                    catch (MethodNotFoundException mnfe2) {
                        MethodExpression me = expressionFactory.createMethodExpression(elContext, command,
                                        String.class, new Class[]{MenuActionEvent.class});
                        invokeResult = me.invoke(elContext, new Object[]{event});
                    }
                }
                finally {
                    String outcome = (invokeResult != null) ? invokeResult.toString() : null;

                    facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, command, outcome);
                }
            }
        }
        else {
            super.broadcast(event);
        }
    }
}
