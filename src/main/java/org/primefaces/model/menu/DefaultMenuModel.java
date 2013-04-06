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
package org.primefaces.model.menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultMenuModel implements MenuModel, Serializable {

    private List<MenuElement> elements;

    public DefaultMenuModel() {
        elements = new ArrayList<MenuElement>();
    }

    public void addSubmenu(Submenu submenu) {
        elements.add(submenu);
    }

    public void addMenuItem(MenuItem menuItem) {
        elements.add(menuItem);
    }

    /*public void addSeparator(Separator separator) {
        elements.add(separator);
    }*/
    
    public List<MenuElement> getElements() {
        return elements;
    }
}