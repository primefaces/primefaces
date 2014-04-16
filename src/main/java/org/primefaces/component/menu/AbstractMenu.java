/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.menu;

import java.util.List;
import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.component.UIPanel;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.MenuActionEvent;

import org.primefaces.model.menu.MenuModel;
import org.primefaces.model.menu.MenuItem;

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
    
    public static final String MOBILE_MENUITEM_LINK_CLASS = "ui-link ui-btn";
    
    protected enum PropertyKeys {
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
        if(model != null)
            return model.getElements();
        else
            return getChildren();
    }
    
    public int getElementsCount() {
        List elements = getElements();
        
        return (elements == null) ? 0 : elements.size();
    }
    
	public abstract MenuModel getModel();
	
	public boolean isDynamic() {
		return this.getValueExpression("model") != null;
	}

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if(event instanceof MenuActionEvent) {
            FacesContext facesContext = getFacesContext();
            ELContext eLContext = facesContext.getELContext();
            MenuActionEvent menuActionEvent = (MenuActionEvent) event;
            MenuItem menuItem = menuActionEvent.getMenuItem();
            String command = menuItem.getCommand();
            
            if(command != null) {
                String actionExpressionString = menuItem.getCommand();
                MethodExpression noArgExpr = facesContext.getApplication().getExpressionFactory().
                                createMethodExpression(eLContext,actionExpressionString, 
                                                            String.class, new Class[0]);
                Object outcome = null;

                try {
                    outcome = noArgExpr.invoke(eLContext, null);
                } 
                catch(MethodNotFoundException methodNotFoundException) {
                    try {
                        MethodExpression argExpr = facesContext.getApplication().getExpressionFactory().
                                createMethodExpression(eLContext, actionExpressionString, 
                                                            String.class, new Class[]{ActionEvent.class});
                        
                        outcome = argExpr.invoke(eLContext, new Object[]{event});
                    }
                    catch(MethodNotFoundException methodNotFoundException2) {
                        MethodExpression argExpr = facesContext.getApplication().getExpressionFactory().
                                createMethodExpression(eLContext, actionExpressionString, 
                                                            String.class, new Class[]{MenuActionEvent.class});
                        
                        outcome = argExpr.invoke(eLContext, new Object[]{event});
                    }
                }
                finally {
                    if(outcome != null) {
                        facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, actionExpressionString, outcome.toString());
                    }
                }
            }
            
        }
        else {
            super.broadcast(event);
        }
    }
}