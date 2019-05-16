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
package org.primefaces.component.menu;

import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.primefaces.event.MenuActionEvent;
import org.primefaces.model.menu.MenuItem;
import org.primefaces.model.menu.MenuModel;
import org.primefaces.util.SerializableFunction;

public abstract class AbstractMenu extends UIPanel {

    public static final String LIST_CLASS = "ui-menu-list ui-helper-reset";
    public static final String MENUITEM_CLASS = "ui-menuitem ui-widget ui-corner-all";
    public static final String MENUITEM_LINK_CLASS = "ui-menuitem-link ui-corner-all";
    public static final String MENUITEM_TEXT_CLASS = "ui-menuitem-text";
    public static final String MENUITEM_ICON_CLASS = "ui-menuitem-icon ui-icon";
    public static final String TIERED_SUBMENU_CLASS = "ui-widget ui-menuitem ui-corner-all ui-menu-parent";
    public static final String TIERED_CHILD_SUBMENU_CLASS = "ui-widget-content ui-menu-list ui-corner-all ui-helper-clearfix ui-menu-child ui-shadow";
    public static final String SUBMENU_RIGHT_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SUBMENU_DOWN_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String SUBMENU_LINK_CLASS = "ui-menuitem-link ui-submenu-link ui-corner-all";
    public static final String SEPARATOR_CLASS = "ui-separator ui-state-default";
    public static final String OPTIONS_CLASS = "ui-menuitem ui-menubar-options ui-widget ui-corner-all";

    public enum PropertyKeys {
        tabindex
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
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

    public abstract MenuModel getModel();

    public boolean isDynamic() {
        return getValueExpression("model") != null;
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
