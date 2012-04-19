/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import javax.faces.component.UIComponentBase;

import org.primefaces.model.MenuModel;

public abstract class AbstractMenu extends UIComponentBase {
		
    public static final String LIST_CLASS = "ui-menu-list ui-helper-reset";
    public static final String MENUITEM_CLASS = "ui-menuitem ui-widget ui-corner-all";
    public static final String MENUITEM_LINK_CLASS = "ui-menuitem-link ui-corner-all";
    public static final String MENUITEM_TEXT_CLASS = "ui-menuitem-text";
    public static final String MENUITEM_ICON_CLASS = "ui-menuitem-icon ui-icon";
    public static final String TIERED_SUBMENU_CLASS = "ui-widget ui-menuitem ui-corner-all ui-menu-parent";
    public static final String TIERED_CHILD_SUBMENU_CLASS = "ui-widget-content ui-menu-list ui-corner-all ui-helper-clearfix ui-menu-child ui-shadow";
    public static final String SUBMENU_RIGHT_ICON_CLASS = "ui-icon ui-icon-triangle-1-e";
    public static final String SUBMENU_DOWN_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";
    public static final String SEPARATOR_CLASS = "ui-separator ui-state-default";
    
	public void buildMenuFromModel() {
		MenuModel model = (MenuModel) getModel();
		
        getChildren().clear();
        
		if(model != null) {
            getChildren().addAll(model.getContents());
		}
	}
	
	public abstract MenuModel getModel();
	
	public boolean isDynamic() {
		return this.getValueExpression("model") != null;
	}
}