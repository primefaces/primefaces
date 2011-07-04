/*
 * Copyright 2009,2010 Prime Technology.
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
package org.primefaces.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;

public class DefaultMenuModel implements MenuModel, Serializable {

	private List<Submenu> submenus;
	private List<MenuItem> menuItems;
	
	public DefaultMenuModel() {
		submenus = new ArrayList<Submenu>();
		menuItems = new ArrayList<MenuItem>();
	}

	public List<Submenu> getSubmenus() {
		return submenus;
	}
	
	public void addSubmenu(Submenu submenu) {
		submenus.add(submenu);
	}

	public void addMenuItem(MenuItem menuItem) {
		menuItems.add(menuItem);
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}
}