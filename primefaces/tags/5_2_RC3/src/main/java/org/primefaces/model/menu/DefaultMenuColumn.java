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
package org.primefaces.model.menu;

import java.util.ArrayList;
import java.util.List;

public class DefaultMenuColumn implements MenuColumn {
    
    private String id;
    private String style;
    private String styleClass;
    private List<MenuElement> elements;
    private boolean rendered = true;
    
    public DefaultMenuColumn() {
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
    
    public boolean isRendered() {
        return rendered;
    }
    public void setRendered(boolean rendered) {
        this.rendered = rendered;
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
    
    public void addElement(MenuElement element) {
        elements.add(element);
    }
}
