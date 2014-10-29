/*
 * Copyright 2009-2012 PrimeTek.
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

public class DefaultSubMenu implements Submenu, Serializable {
    
    private String id;
    private String style;
    private String styleClass;
    private String icon;
    private String label;
    private List<MenuElement> elements;
    private boolean rendered = true;
    private boolean expanded = false;
    
    public DefaultSubMenu() {
        elements = new ArrayList<MenuElement>();
    }
    
    public DefaultSubMenu(String label) {
        this.label = label;
        elements = new ArrayList<MenuElement>();
    }
    
    public DefaultSubMenu(String label, String icon) {
        this.label = label;
        this.icon = icon;
        elements = new ArrayList<MenuElement>();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getStyle() {
        return style;
    }
    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return styleClass;
    }
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }

    public List<MenuElement> getElements() {
        return elements;
    }

    public void setElements(List<MenuElement> elements) {
        this.elements = elements;
    } 
    
    public int getElementsCount() {
        return (elements == null) ? 0 : elements.size();
    }
    
    public boolean isRendered() {
        return rendered;
    }
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
    }
    
    public boolean isExpanded() {
        return expanded;
    }
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Object getParent() {
        return null;
    }
    
    public void addElement(MenuElement element) {
        elements.add(element);
    }

    public String getClientId() {
        return this.id;
    }
}
